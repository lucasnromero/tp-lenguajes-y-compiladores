package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;

import lyc.compiler.tercetos.TercetoManager;

public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        var tercetos = TercetoManager.getAll();
        
        for (int i = 0; i < tercetos.size(); i++) {
            fileWriter.write("[" + i + "] " + tercetos.get(i).toString() + "\n");
        }
    }
}
