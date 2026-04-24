package lyc.compiler;

import lyc.compiler.factories.LexerFactory;
import lyc.compiler.model.CompilerException;
import lyc.compiler.model.InvalidIntegerException;
import lyc.compiler.model.InvalidLengthException;
import lyc.compiler.model.UnknownCharacterException;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.constants.Constants.MAX_LENGTH;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class LexerTest {

  private Lexer lexer;

  // ─────────────────────────────────────────────
  // Comments
  // ─────────────────────────────────────────────

  @Test
  public void singleLineComment() throws Exception {
    scan("#+ esto es un comentario  汉字汉字+#");
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void multilineComment() throws Exception {
    scan("#+ linea uno\nlinea dos +#");
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void commentBetweenTokens() throws Exception {
    scan("x #+ comentario +# y");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Reserved words
  // ─────────────────────────────────────────────

  @Test
  public void keywordIf() throws Exception {
    scan("if");
    assertThat(nextToken()).isEqualTo(sym.IF);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordElse() throws Exception {
    scan("else");
    assertThat(nextToken()).isEqualTo(sym.ELSE);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordWhile() throws Exception {
    scan("while");
    assertThat(nextToken()).isEqualTo(sym.WHILE);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordRead() throws Exception {
    scan("read");
    assertThat(nextToken()).isEqualTo(sym.READ);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordWrite() throws Exception {
    scan("write");
    assertThat(nextToken()).isEqualTo(sym.WRITE);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordInit() throws Exception {
    scan("init");
    assertThat(nextToken()).isEqualTo(sym.INIT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Type keywords
  // ─────────────────────────────────────────────

  @Test
  public void keywordInt() throws Exception {
    scan("Int");
    assertThat(nextToken()).isEqualTo(sym.INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordFloat() throws Exception {
    scan("Float");
    assertThat(nextToken()).isEqualTo(sym.FLOAT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void keywordString() throws Exception {
    scan("String");
    assertThat(nextToken()).isEqualTo(sym.STRING);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Logical operators
  // ─────────────────────────────────────────────

  @Test
  public void operatorAnd() throws Exception {
    scan("AND");
    assertThat(nextToken()).isEqualTo(sym.AND);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorOr() throws Exception {
    scan("OR");
    assertThat(nextToken()).isEqualTo(sym.OR);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorNot() throws Exception {
    scan("NOT");
    assertThat(nextToken()).isEqualTo(sym.NOT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Arithmetic operators
  // ─────────────────────────────────────────────

  @Test
  public void operatorPlus() throws Exception {
    scan("+");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MAS);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorMinus() throws Exception {
    scan("-");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOS);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorMultiply() throws Exception {
    scan("*");
    assertThat(nextToken()).isEqualTo(sym.ASTERISCO);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorDivide() throws Exception {
    scan("/");
    assertThat(nextToken()).isEqualTo(sym.BARRA_DIAGONAL);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Comparison operators
  // ─────────────────────────────────────────────

  @Test
  public void operatorEqual() throws Exception {
    scan("==");
    assertThat(nextToken()).isEqualTo(sym.EQ);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorNotEqual() throws Exception {
    scan("!=");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_DISTINTO);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorLessThan() throws Exception {
    scan("<");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOR);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorGreaterThan() throws Exception {
    scan(">");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MAYOR);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorLessOrEqual() throws Exception {
    scan("<=");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOR_O_IGUAL);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorGreaterOrEqual() throws Exception {
    scan(">=");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MAYOR_O_IGUAL);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Assignment and colon
  // ─────────────────────────────────────────────

  @Test
  public void operatorAssignment() throws Exception {
    scan(":=");
    assertThat(nextToken()).isEqualTo(sym.ASIG);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void operatorColon() throws Exception {
    scan(":");
    assertThat(nextToken()).isEqualTo(sym.DOS_PUNTOS);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Delimiters
  // ─────────────────────────────────────────────

  @Test
  public void delimiterOpenParen() throws Exception {
    scan("(");
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_IZQ);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void delimiterCloseParen() throws Exception {
    scan(")");
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_DER);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void delimiterOpenBrace() throws Exception {
    scan("{");
    assertThat(nextToken()).isEqualTo(sym.LLAVE_IZQ);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void delimiterCloseBrace() throws Exception {
    scan("}");
    assertThat(nextToken()).isEqualTo(sym.LLAVE_DER);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void delimiterComma() throws Exception {
    scan(",");
    assertThat(nextToken()).isEqualTo(sym.COMA);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Integer constants
  // ─────────────────────────────────────────────

  @Test
  public void integerZero() throws Exception {
    scan("0");
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void integerMaxPositive() throws Exception {
    scan("32767");
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void invalidPositiveIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("%d".formatted(9223372036854775807L));
      nextToken();
    });
  }

  @Test
  public void invalidNegativeIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("%d".formatted(-9223372036854775807L));
      nextToken();
    });
  }

  @Test
  public void integerJustOverMaxPositive() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("32768");
      nextToken();
    });
  }

  // ─────────────────────────────────────────────
  // Negative integer: pushback behavior
  // ─────────────────────────────────────────────

  @Test
  public void negativeIntegerLexedAsTwoTokens() throws Exception {
    // -5 → SIMBOLO_MENOS then CTE_INT (pushback de los dígitos)
    scan("-5");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOS);
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void negativeLargeValidValue() throws Exception {
    // -32767 está en rango y el pushback de "32767" también es válido como CTE_INT
    scan("-32767");
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOS);
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void negativeJustOutOfRange() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("-32769");
      nextToken();
    });
  }

  // ─────────────────────────────────────────────
  // Float constants
  // ─────────────────────────────────────────────

  @Test
  public void floatWithBothSides() throws Exception {
    scan("3.14");
    assertThat(nextToken()).isEqualTo(sym.CTE_FLOAT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void floatWithLeadingDot() throws Exception {
    scan(".5");
    assertThat(nextToken()).isEqualTo(sym.CTE_FLOAT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void floatWithTrailingDot() throws Exception {
    scan("3.");
    assertThat(nextToken()).isEqualTo(sym.CTE_FLOAT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // String constants
  // ─────────────────────────────────────────────

  @Test
  public void validStringConstant() throws Exception {
    scan("\"hola mundo\"");
    assertThat(nextToken()).isEqualTo(sym.CTE_STR);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void emptyStringConstant() throws Exception {
    scan("\"\"");
    assertThat(nextToken()).isEqualTo(sym.CTE_STR);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void invalidStringConstantLength() {
    assertThrows(InvalidLengthException.class, () -> {
      scan("\"%s\"".formatted(getRandomString()));
      nextToken();
    });
  }

  // ─────────────────────────────────────────────
  // Identifiers
  // ─────────────────────────────────────────────

  @Test
  public void validIdentifier() throws Exception {
    scan("miVariable");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void identifierWithDigits() throws Exception {
    scan("var123");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void invalidIdLength() {
    assertThrows(InvalidLengthException.class, () -> {
      scan(getRandomString());
      nextToken();
    });
  }

  // ─────────────────────────────────────────────
  // Unknown characters
  // ─────────────────────────────────────────────

  @Test
  public void unknownCharacter() {
    assertThrows(UnknownCharacterException.class, () -> {
      scan("@");
      nextToken();
    });
  }

  @Test
  public void unknownCharacterDollar() {
    assertThrows(UnknownCharacterException.class, () -> {
      scan("$");
      nextToken();
    });
  }

  @Test
  public void unknownCharacterBang() {
    assertThrows(UnknownCharacterException.class, () -> {
      scan("!");
      nextToken();
    });
  }

  // ─────────────────────────────────────────────
  // Multi-token sequences
  // ─────────────────────────────────────────────

  @Test
  public void assignmentWithExpressions() throws Exception {
    scan("c := d * (e - 21) / 4");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.ASIG);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.ASTERISCO);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_IZQ);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOS);
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_DER);
    assertThat(nextToken()).isEqualTo(sym.BARRA_DIAGONAL);
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void declarationTokenSequence() throws Exception {
    scan("x , y : Int");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.COMA);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.DOS_PUNTOS);
    assertThat(nextToken()).isEqualTo(sym.INT);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void conditionWithLogicalOperators() throws Exception {
    scan("a == b AND c > d OR NOT e <= f");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EQ);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.AND);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MAYOR);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.OR);
    assertThat(nextToken()).isEqualTo(sym.NOT);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOR_O_IGUAL);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void conditionWithLogicalOperatorsAndNotEqual() throws Exception {
    scan("a != b AND c > d OR NOT e <= f");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_DISTINTO);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.AND);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MAYOR);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.OR);
    assertThat(nextToken()).isEqualTo(sym.NOT);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MENOR_O_IGUAL);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void writeStatement() throws Exception {
    scan("write(\"hola\")");
    assertThat(nextToken()).isEqualTo(sym.WRITE);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_IZQ);
    assertThat(nextToken()).isEqualTo(sym.CTE_STR);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_DER);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void readStatement() throws Exception {
    scan("read(myVar)");
    assertThat(nextToken()).isEqualTo(sym.READ);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_IZQ);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_DER);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void whileLoop() throws Exception {
    scan("while ( x >= 0 ) { }");
    assertThat(nextToken()).isEqualTo(sym.WHILE);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_IZQ);
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.SIMBOLO_MAYOR_O_IGUAL);
    assertThat(nextToken()).isEqualTo(sym.CTE_INT);
    assertThat(nextToken()).isEqualTo(sym.PARENTESIS_DER);
    assertThat(nextToken()).isEqualTo(sym.LLAVE_IZQ);
    assertThat(nextToken()).isEqualTo(sym.LLAVE_DER);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  @Test
  public void whitespaceIsIgnored() throws Exception {
    scan("  \t\n  x  \t\n  ");
    assertThat(nextToken()).isEqualTo(sym.ID);
    assertThat(nextToken()).isEqualTo(sym.EOF);
  }

  // ─────────────────────────────────────────────
  // Helpers
  // ─────────────────────────────────────────────

  @AfterEach
  public void resetLexer() {
    lexer = null;
  }

  private void scan(String input) {
    lexer = LexerFactory.create(input);
  }

  private int nextToken() throws IOException, CompilerException {
    return lexer.next_token().sym;
  }

  private static String getRandomString() {
    return new RandomStringGenerator.Builder()
            .filteredBy(CharacterPredicates.LETTERS)
            .withinRange('a', 'z')
            .build().generate(MAX_LENGTH * 2);
  }

}
