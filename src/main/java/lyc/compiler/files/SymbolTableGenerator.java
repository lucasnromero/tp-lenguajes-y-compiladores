package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.symbolTable.SymbolTable;

public class SymbolTableGenerator implements FileGenerator{

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write("LEXEMA\tTIPO\tVALOR\n");

        for (var entry : SymbolTable.getSymbols().values()) {
            String lexema = entry.getName();
            String tipo = entry.getType() != null ? entry.getType() : "-";
            String valor = entry.getValue() != null ? entry.getValue().toString() : "-";

        fileWriter.write(lexema + "\t" + tipo + "\t" + valor + "\n");
        }
    }
}
