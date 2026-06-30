package lyc.compiler.tercetos;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import lyc.compiler.symbolTable.*;

public class TercetoManager {
    private static final List<Terceto> tercetos = new ArrayList<>();
    private static Stack<Integer> auxStack = new Stack<>();
    private static boolean hasDeclaredVariables = false;  // Rastrear si hay declaraciones

    public static void reset() {
        tercetos.clear();
        auxStack.clear();
        hasDeclaredVariables = false;
    }

    public static void setHasDeclaredVariables(boolean value) {
        hasDeclaredVariables = value;
    }

    public static boolean getHasDeclaredVariables() {
        return hasDeclaredVariables;
    }

    public static int add(String op, String arg1, String arg2) {
        String type = inferType(op, arg1, arg2);
        tercetos.add(new Terceto(op, arg1, arg2, type));
        return tercetos.size() - 1; // índice
    }

    /**
     * Adds a terceto with constant folding optimization.
     * If both arguments are constants, evaluates the expression and returns the constant result.
     * Otherwise, creates a terceto and returns its reference.
     * 
     * NOTE: Constant folding is disabled for debugging. Uncomment to enable.
     */
    public static String addFolded(String op, String arg1, String arg2) {
        // Disable constant folding for now to see all tercetos
        // if (ConstantFolder.isConstant(arg1) && ConstantFolder.isConstant(arg2)) {
        //     // Both are constants, fold them
        //     return ConstantFolder.evaluate(op, arg1, arg2);
        // } else {
            // Create terceto and return reference
            int index = add(op, arg1, arg2);
            return ref(index);
        // }
    }

    public static String ref(int index) {
        return "[" + index + "]";
    }

    /**
     * Reemplaza la lista de tercetos con una versión optimizada.
     * Usado después de eliminación de código muerto.
     */
    public static void setOptimized(List<Terceto> optimizedTercetos) {
        tercetos.clear();
        tercetos.addAll(optimizedTercetos);
    }

    public static List<Terceto> getAll() {
        return tercetos;
    }

    public static Terceto get(int index) {
        return tercetos.get(index);
    }

    public static Terceto get(String ref) {
        return tercetos.get(Integer.parseInt(ref.substring(1, ref.length() - 1)));
    }

    public static int nextIndex() {
    return tercetos.size();
    }

    public static void patch(int index, int argPos, String value) {
        Terceto t = tercetos.get(index);
        if(argPos == 0) t.setOp(value);
        if(argPos == 1) t.setArg1(value);
        if(argPos == 2) t.setArg2(value);
    }

    public static void pushIndex(int index) {
    auxStack.push(index);
}

public static int popIndex() {
    return auxStack.pop();
}

    private static String inferType(String op, String arg1, String arg2) {
        switch (op) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "DIV":
            case "MOD":
                String type1 = getArgType(arg1);
                String type2 = getArgType(arg2);
                // Solo validar si ambos tipos son conocidos
                if (type1 != null && type2 != null) {
                    if (!isNumeric(type1) || !isNumeric(type2)) {
                        throw new RuntimeException("Operación aritmética requiere tipos numéricos: " + type1 + " y " + type2);
                    }
                    return (type1.equals("Float") || type2.equals("Float")) ? "Float" : "Int";
                }
                // Si uno es nulo, intentar inferir del otro
                if (type1 != null && isNumeric(type1)) return type1;
                if (type2 != null && isNumeric(type2)) return type2;
                // Si ambos son nulos o no numéricos, asumir Int por defecto
                return "Int";
            case ":=":
                return getArgType(arg2);
            case "==":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "!=":
                // Comparaciones devuelven boolean implícitamente
                return "Boolean";
            case "WRITE":
            case "READ":
                return getArgType(arg1);
            case "BF":
            case "BI":
            case "NOT":
            case "AND":
            case "OR":
                return null; // No aplicable
            default:
                return null;
        }
    }

    private static String getArgType(String arg) {
        if (arg == null || arg.equals("-")) return null;
        if (arg.startsWith("[")) {
            // Referencia a terceto
            int index = Integer.parseInt(arg.substring(1, arg.length() - 1));
            return tercetos.get(index).getType();
        }
        // Verificar si es constante
        try {
            Integer.parseInt(arg);
            return "Int";
        } catch (NumberFormatException e1) {
            try {
                Double.parseDouble(arg);
                return "Float";
            } catch (NumberFormatException e2) {
                // Es ID
                SymbolTableEntry entry = SymbolTable.get(arg);
                return (entry != null) ? entry.getType() : null;
            }
        }
    }

    private static boolean isNumeric(String type) {
        return "Int".equals(type) || "Float".equals(type);
    }

    public static int getIndex(String ref){
        return Integer.parseInt(ref.substring(1, ref.length() - 1));
    }
}
