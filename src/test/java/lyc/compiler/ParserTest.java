package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.factories.ParserFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.Constants.EXAMPLES_ROOT_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ParserTest {

    @Test
    public void forTest() throws Exception {
        compilationSuccessful("for  a:=3 to 5 b:=3 b:=3+5 next a");
    }

    @Test
    public void forWithStepTest() throws Exception {
        compilationSuccessful("for  i:=0 to 5 step 2 read(b) b:=3+5 next i");
    }

    @Test
    public void divAndMod() throws Exception {
        compilationSuccessful("x := (10 div 3) + (10 mod 3)");
    }

    @Test
    public void notEqualComparatorInCondition() throws Exception {
        compilationSuccessful("if (a != b) { x := 1 }");
    }

    @Test
    public void syntaxError() {
        compilationError("1234");
    }

    @Test
    void assignments() throws Exception {
        compilationSuccessful(readFromFile("assignments.txt"));
    }

    @Test
    void write() throws Exception {
        compilationSuccessful(readFromFile("write.txt"));
    }

    @Test
    void read() throws Exception {
        compilationSuccessful(readFromFile("read.txt"));
    }

    @Test
    void comment() throws Exception {
        compilationSuccessful(readFromFile("comment.txt"));
    }

    @Test
    void init() throws Exception {
        compilationSuccessful(readFromFile("init.txt"));
    }

    @Test
    void and() throws Exception {
        compilationSuccessful(readFromFile("and.txt"));
    }

    @Test
    void or() throws Exception {
        compilationSuccessful(readFromFile("or.txt"));
    }

    @Test
    void not() throws Exception {
        compilationSuccessful(readFromFile("not.txt"));
    }

    @Test
    void ifStatement() throws Exception {
        compilationSuccessful(readFromFile("if.txt"));
    }

    @Test
    void whileStatement() throws Exception {
        compilationSuccessful(readFromFile("while.txt"));
    }

    @Test
    public void stepErrorFloat() {
        compilationError("for i := 1 to 10 step 2.5 write(i) next i");
    }
    @Test
    public void stepNegativeError() {
        compilationError("for i := 1 to 10 step -1 write(i) next i");
    }

    @Test
    public void divError() {
        compilationError("x := 'hola' div 2");
    }


    @Test
    public void toError() {
        compilationError("for i := 1 10 write(i) next i");
    }

    @Test
    public void asignacionError() {
        compilationError("for i 1 to 10 write(i) next i");
    }

    //@Test
    //public void variableNextError() {
    //    compilationError("for i := 1 to 5 write(i) next j");
    //}

    //@Test
    //public void divisionByZeroError() {
    //    compilationError("x := 10 / 0");
    //}


    private void compilationSuccessful(String input) throws Exception {
        assertThat(scan(input).sym).isEqualTo(sym.EOF);
    }

    private void compilationError(String input){
        assertThrows(Exception.class, () -> scan(input));
    }

    private Symbol scan(String input) throws Exception {
        return ParserFactory.create(input).parse();
    }

    private String readFromFile(String fileName) throws IOException {
        URL url = new URL(EXAMPLES_ROOT_DIRECTORY + "/%s".formatted(fileName));
        assertThat(url).isNotNull();
        return IOUtils.toString(url.openStream(), StandardCharsets.UTF_8);
    }

}
