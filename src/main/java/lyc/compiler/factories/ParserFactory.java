package lyc.compiler.factories;

import lyc.compiler.parser;
import lyc.compiler.tercetos.TercetoManager;
import lyc.compiler.symbolTable.SymbolTable;

import java.io.Reader;

public final class ParserFactory {

    private ParserFactory(){}

    public static parser create(String input) {
        TercetoManager.reset();
        SymbolTable.reset();
        return new parser(LexerFactory.create(input));
    }

    public static parser create(Reader reader) {
        TercetoManager.reset();
        SymbolTable.reset();
        return new parser(LexerFactory.create(reader));
    }


}
