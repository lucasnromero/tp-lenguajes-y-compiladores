package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.symbolTable.SymbolTable;

public class SymbolTableGenerator implements FileGenerator{

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(String.format("%-15s %-10s %-15s %-10s%n",
            "LEXEMA", "TIPO", "VALOR", "LONGITUD"));

        for (var entry : SymbolTable.getSymbols().values()) {
            String longitud = entry.getType() != null ? String.valueOf(entry.getLength()) : "-";
            String lexema = entry.getName();
            String tipo = entry.getType() != null ? entry.getType() : "-";
            String valor = entry.getValue() != null ? entry.getValue().toString() : "-";

            fileWriter.write(String.format("%-15s %-10s %-15s %-10s%n",
                lexema, tipo, valor, longitud));
        }
    }
}
