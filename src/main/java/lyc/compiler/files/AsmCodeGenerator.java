package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lyc.compiler.symbolTable.SymbolTable;
import lyc.compiler.symbolTable.SymbolTableEntry;
import lyc.compiler.tercetos.Terceto;
import lyc.compiler.tercetos.TercetoManager;

public class AsmCodeGenerator implements FileGenerator {

    private static String safeLabel(String name, Map<String, String> existing) {
        if (name == null || name.isBlank()) {
            name = "_";
        }
        String label = name.replaceAll("[^A-Za-z0-9]", "_");
        if (!label.startsWith("_")) {
            label = "_" + label;
        }
        String base = label;
        int suffix = 1;
        while (existing.containsValue(label)) {
            label = base + "_" + suffix++;
        }
        return label;
    }

    private static String escapeStringLiteral(String value) {
        return value.replace("\"", "\"\"");
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        var symbols = SymbolTable.getSymbols();
        List<Terceto> tercetos = TercetoManager.getAll();

        String nl = System.lineSeparator();

        // Header
        fileWriter.write("include macros2.asm" + nl);
        fileWriter.write("include number.asm" + nl + nl);
        fileWriter.write(".MODEL  LARGE" + nl);
        fileWriter.write(".386" + nl);
        fileWriter.write(".STACK 200h" + nl + nl);
        fileWriter.write(".DATA" + nl + nl);

        // Symbol table entries
        Map<String, String> symLabel = new LinkedHashMap<>();
        for (var entry : symbols.values()) {
            String name = entry.getName();
            String label = safeLabel(name, symLabel);
            symLabel.put(name, label);

            if ("String".equals(entry.getType()) || "CTE_STR".equals(entry.getType())) {
                String val = entry.getValue() != null ? entry.getValue().toString() : "";
                if (entry.getValue() != null) {
                    fileWriter.write(String.format("    %s db  \"%s\",'$'%n", label, escapeStringLiteral(val)));
                } else {
                    int len = Math.max(1, entry.getLength());
                    fileWriter.write(String.format("    %s db  %d dup (?)%n", label, len + 1));
                }
            } else {
                if (entry.getValue() != null) {
                    fileWriter.write(String.format("    %s dd  %s%n", label, entry.getValue().toString()));
                } else {
                    fileWriter.write(String.format("    %s dd  ?%n", label));
                }
            }
        }

        // Collect numeric constants and create labels
        Map<String, String> constLabel = new LinkedHashMap<>();
        java.util.concurrent.atomic.AtomicInteger constCount = new java.util.concurrent.atomic.AtomicInteger(0);
        for (int i = 0; i < tercetos.size(); i++) {
            Terceto tc = tercetos.get(i);
            String[] args = {tc.getArg1(), tc.getArg2()};
            for (String a : args) {
                if (a == null) continue;
                if (a.startsWith("[")) continue; // terceto ref
                if (symbols.containsKey(a)) continue; // symbol
                // numeric literal?
                try {
                    Double.parseDouble(a);
                    if (!constLabel.containsKey(a)) {
                        String cl = "_c" + constCount.getAndIncrement();
                        constLabel.put(a, cl);
                    }
                } catch (NumberFormatException e) {
                    // not a number, ignore
                }
            }
        }

        // Write constants
        for (var e : constLabel.entrySet()) {
            fileWriter.write(String.format("    %s dd  %s%n", e.getValue(), e.getKey()));
        }

        // Create temporals for each terceto (store result)
        for (int i = 0; i < tercetos.size(); i++) {
            fileWriter.write(String.format("    _t%d dd  ?%n", i));
        }

        // Some helper strings
        fileWriter.write("    _NEWLINE db 0DH,0AH,'$'" + nl + nl);

        // Code section
        fileWriter.write(".CODE" + nl + nl);
        fileWriter.write("START:" + nl);
        fileWriter.write("    mov AX,@DATA" + nl);
        fileWriter.write("    mov DS,AX" + nl);
        fileWriter.write("    mov es,ax" + nl + nl);

        // Map terceto index to its temporary label
        Map<Integer, String> tercToLabel = new LinkedHashMap<>();
        for (int i = 0; i < tercetos.size(); i++) tercToLabel.put(i, "_t" + i);

        // Helper to resolve an argument to a label
        java.util.function.Function<String, String> resolve = (arg) -> {
            if (arg == null) return null;
            arg = arg.trim();
            if (arg.startsWith("[")) {
                int idx = Integer.parseInt(arg.substring(1, arg.length() - 1));
                return tercToLabel.get(idx);
            }
            if (symLabel.containsKey(arg)) return symLabel.get(arg);
            if (symLabel.containsKey("_" + arg)) return symLabel.get("_" + arg);
            if (constLabel.containsKey(arg)) return constLabel.get(arg);
            // If numeric literal not captured, create ad-hoc label (shouldn't happen)
            try {
                Double.parseDouble(arg);
                String cl = "_c" + constCount.getAndIncrement();
                constLabel.put(arg, cl);
                return cl;
            } catch (Exception e) {
                return "_" + arg; // fallback
            }
        };

        for (int i = 0; i < tercetos.size(); i++) {
            Terceto t = tercetos.get(i);
            String op = t.getOp();
            String a1 = t.getArg1();
            String a2 = t.getArg2();
            String r = tercToLabel.get(i);

            // Emit label for this terceto (targets jump labels will point here)
            fileWriter.write(String.format("ET_%d:" + nl, i));
            fileWriter.write(String.format("    ; [%d] %s%n", i, t.toString()));

            switch (op) {
                case "+":
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a1)));
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a2)));
                    fileWriter.write("    faddp" + nl);
                    fileWriter.write(String.format("    fstp dword ptr [%s]%n", r));
                    break;
                case "-":
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a1)));
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a2)));
                    fileWriter.write("    fsubp" + nl);
                    fileWriter.write(String.format("    fstp dword ptr [%s]%n", r));
                    break;
                case "*":
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a1)));
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a2)));
                    fileWriter.write("    fmulp" + nl);
                    fileWriter.write(String.format("    fstp dword ptr [%s]%n", r));
                    break;
                case "/":
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a1)));
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", resolve.apply(a2)));
                    fileWriter.write("    fdivp" + nl);
                    fileWriter.write(String.format("    fstp dword ptr [%s]%n", r));
                    break;
                case "CMP": {
                    // Compare a1 with a2 using FPU and set flags
                    String op1 = resolve.apply(a1);
                    String op2 = resolve.apply(a2);
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", op1));
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", op2));
                    fileWriter.write("    fxch" + nl);
                    fileWriter.write("    fcomp" + nl);
                    fileWriter.write("    fstsw ax" + nl);
                    fileWriter.write("    ffree st(0)" + nl);
                    fileWriter.write("    sahf" + nl);
                    break;
                }
                case "BF":
                    // Branch if false -> use JBE as default (matches macros usage)
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    jbe ET_%d%n", target));
                    }
                    break;
                case "BI":
                    // Unconditional jump
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    jmp ET_%d%n", target));
                    }
                    break;
                case "BNE":
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    jne ET_%d%n", target));
                    }
                    break;
                case "BLE":
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    jbe ET_%d%n", target));
                    }
                    break;
                case "BGE":
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    jae ET_%d%n", target));
                    }
                    break;
                case "BLT":
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    jb ET_%d%n", target));
                    }
                    break;
                case "BGT":
                    if (a1 != null && a1.startsWith("[")) {
                        int target = Integer.parseInt(a1.substring(1, a1.length() - 1));
                        fileWriter.write(String.format("    ja ET_%d%n", target));
                    }
                    break;
                case "AND": {
                    // Boolean AND with short-circuit: if a1 is false, skip a2 and store 0
                    String la1 = resolve.apply(a1);
                    String la2 = resolve.apply(a2);
                    fileWriter.write(String.format("    mov eax, dword ptr [%s]%n", la1));
                    fileWriter.write("    test eax, eax" + nl);
                    fileWriter.write(String.format("    je AND_FALSE_%d%n", i));
                    fileWriter.write(String.format("    mov ebx, dword ptr [%s]%n", la2));
                    fileWriter.write("    test ebx, ebx" + nl);
                    fileWriter.write(String.format("    je AND_FALSE_%d%n", i));
                    fileWriter.write("    mov eax, 1" + nl);
                    fileWriter.write(String.format("    jmp AND_END_%d%n", i));
                    fileWriter.write(String.format("AND_FALSE_%d:" + nl, i));
                    fileWriter.write("    mov eax, 0" + nl);
                    fileWriter.write(String.format("AND_END_%d:" + nl, i));
                    fileWriter.write(String.format("    mov dword ptr [%s], eax%n", r));
                    break;
                }
                case "OR": {
                    // Boolean OR with short-circuit: if a1 is true, skip a2 and store 1
                    String la1 = resolve.apply(a1);
                    String la2 = resolve.apply(a2);
                    fileWriter.write(String.format("    mov eax, dword ptr [%s]%n", la1));
                    fileWriter.write("    test eax, eax" + nl);
                    fileWriter.write(String.format("    jne OR_TRUE_%d%n", i));
                    fileWriter.write(String.format("    mov ebx, dword ptr [%s]%n", la2));
                    fileWriter.write("    test ebx, ebx" + nl);
                    fileWriter.write(String.format("    jne OR_TRUE_%d%n", i));
                    fileWriter.write("    mov eax, 0" + nl);
                    fileWriter.write(String.format("    jmp OR_END_%d%n", i));
                    fileWriter.write(String.format("OR_TRUE_%d:" + nl, i));
                    fileWriter.write("    mov eax, 1" + nl);
                    fileWriter.write(String.format("OR_END_%d:" + nl, i));
                    fileWriter.write(String.format("    mov dword ptr [%s], eax%n", r));
                    break;
                }
                case "NOT": {
                    // Boolean NOT: r = !a1
                    String la = resolve.apply(a1);
                    fileWriter.write(String.format("    mov eax, dword ptr [%s]%n", la));
                    fileWriter.write("    test eax, eax" + nl);
                    fileWriter.write("    sete al" + nl);
                    fileWriter.write("    movzx eax, al" + nl);
                    fileWriter.write(String.format("    mov dword ptr [%s], eax%n", r));
                    break;
                }
                case ":=": {
                    // assignment: arg1 := arg2
                    String dest = resolve.apply(a1);
                    String src = resolve.apply(a2);
                    fileWriter.write(String.format("    fld dword ptr [%s]%n", src));
                    fileWriter.write(String.format("    fstp dword ptr [%s]%n", dest));
                    break;
                }
                case "READ": {
                    if (a1 != null) {
                        String l = resolve.apply(a1);
                        // determine type if known
                        if (symbols.containsKey(a1)) {
                            SymbolTableEntry se = symbols.get(a1);
                            String typ = se.getType();
                            if ("String".equals(typ)) {
                                fileWriter.write(String.format("    getString %s%n", l));
                            } else if ("Int".equals(typ)) {
                                fileWriter.write(String.format("    GetInteger %s%n", l));
                            } else {
                                // default to float
                                fileWriter.write(String.format("    GetFloat %s%n", l));
                            }
                        } else {
                            // unknown symbol: try numeric
                            fileWriter.write(String.format("    GetFloat %s%n", l));
                        }
                    }
                    break;
                }
                case "WRITE": {
                    if (a1 != null) {
                        String l = resolve.apply(a1);
                        if (symbols.containsKey(a1)) {
                            SymbolTableEntry se = symbols.get(a1);
                            String typ = se.getType();
                            if ("String".equals(typ)) {
                                fileWriter.write(String.format("    mov dx,OFFSET %s%n", l));
                                fileWriter.write("    mov ah,9" + nl);
                                fileWriter.write("    int 21h" + nl);
                            } else if ("Int".equals(typ)) {
                                fileWriter.write(String.format("    DisplayInteger %s%n", l));
                            } else {
                                fileWriter.write(String.format("    DisplayFloat %s, 6%n", l));
                            }
                        } else if (constLabel.containsKey(a1)) {
                            // numeric constant
                            fileWriter.write(String.format("    DisplayFloat %s, 6%n", l));
                        } else {
                            // fallback: try printing as string
                            fileWriter.write(String.format("    mov dx,OFFSET %s%n", l));
                            fileWriter.write("    mov ah,9" + nl);
                            fileWriter.write("    int 21h" + nl);
                        }
                    }
                    break;
                }
                default:
                    fileWriter.write("    ; Unsupported op: " + op + nl);
                    break;
            }
            fileWriter.write(nl);
        }

        // Ensure a final jump target label exists for any BI/BF that points to the end
        if (tercetos.size() > 0) {
            fileWriter.write(String.format("ET_%d:" + nl, tercetos.size()));
            fileWriter.write("    ; Final program exit label" + nl + nl);
        }

        // Program end
        fileWriter.write("    mov ax, 4C00h" + nl);
        fileWriter.write("    int 21h" + nl);
        fileWriter.write("END START" + nl);
    }

}
