package lyc.compiler.symbolTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    private static final Map<String, SymbolTableEntry> symbols = new LinkedHashMap<>();

    public static void add(String lexeme, SymbolTableEntry entry) {
        symbols.putIfAbsent(lexeme, entry);
    }

    public static Map<String, SymbolTableEntry> getSymbols() {
        return symbols;
    }

    public static SymbolTableEntry get(String lexeme) {
        return symbols.get(lexeme);
    }

    public static void reset() {
        symbols.clear();
    }
}