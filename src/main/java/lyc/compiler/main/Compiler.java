package lyc.compiler.main;

import lyc.compiler.parser;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.SymbolTableGenerator;
import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.AsmCodeGenerator;
import lyc.compiler.tercetos.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class Compiler {

    private Compiler(){}

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Filename must be provided as argument.");
            System.exit(0);
        }

        try (Reader reader = FileFactory.create(args[0])) {
            parser parser = ParserFactory.create(reader);
            parser.parse();

            // Fase de optimización: eliminación de código muerto
            List<Terceto> original = new ArrayList<>(TercetoManager.getAll());
          //  List<Terceto> optimized = DeadCodeEliminator.eliminate(TercetoManager.getAll());
           // TercetoManager.setOptimized(optimized);
            
            System.out.println("Tercetos originales: " + original.size());
           // System.out.println("Tercetos optimizados: " + optimized.size());

            // Imprimir tercetos originales
            System.out.println("\n=== TERCETOS ORIGINALES ===");
            for (int j = 0; j < original.size(); j++) {
                System.out.println("[" + j + "] " + original.get(j).toString());
            }

            // Imprimir tercetos optimizados
           // System.out.println("\n=== TERCETOS OPTIMIZADOS ===");
            //for (int j = 0; j < optimized.size(); j++) {
              //  System.out.println("[" + j + "] " + optimized.get(j).toString());
            //}
            FileOutputWriter.writeOutput("symbol-table.txt", new SymbolTableGenerator());
            FileOutputWriter.writeOutput("intermediate-code.txt", new IntermediateCodeGenerator());
           // FileOutputWriter.writeOutput("final.asm", new AsmCodeGenerator());
        } catch (IOException e) {
            System.err.println("There was an error trying to read input file " + e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            System.exit(0);
        }

        System.out.println("Compilation Successful");

    }

}
