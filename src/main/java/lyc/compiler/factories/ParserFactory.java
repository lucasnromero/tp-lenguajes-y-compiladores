package lyc.compiler.factories;

import lyc.compiler.parser;

import java.io.Reader;

public final class ParserFactory {

    private ParserFactory(){}

    public static parser create(String input) {
        return new parser(LexerFactory.create(input));
    }

    public static parser create(Reader reader) {
        return new parser(LexerFactory.create(reader));
    }


}
