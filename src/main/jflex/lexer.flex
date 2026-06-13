package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.sym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import lyc.compiler.symbolTable.*;


%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%state COMMENT
%eofval{
  if (yystate() == COMMENT) {
    throw new InvalidCommentException("Comentario sin cerrar: falta « +# »");
  }
  return symbol(sym.EOF);
%eofval}

%{
  private int commentDepth = 0;

  private Symbol symbol(int type) {
    System.out.print("(" + sym.terminalNames[type] + ",-)" + " ");
    return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(int type, Object value) {
    System.out.print("(" + sym.terminalNames[type] + "," + value + ")" + " ");
    return new Symbol(type, yyline, yycolumn, value);
  } 

  private int parseIntInRange(String text) throws InvalidIntegerException {
    try {
      long value = Long.parseLong(text);
      if (value < -32768 || value > 32767) {
        throw new InvalidIntegerException("Integer out of range");
      }
      SymbolTableEntry entry = new SymbolTableEntry("_"+yytext());
      entry.setType("CTE_INT");
      entry.setValue(Integer.parseInt(yytext()));
      SymbolTable.add("_"+yytext(), entry);
      return (int) value;
    } catch (NumberFormatException e) {
      throw new InvalidIntegerException("Invalid integer");
    }
  }

  private float parseFloatInRange(String text) throws InvalidFloatException {
    try {
      float value = Float.parseFloat(text);
      if (!Float.isFinite(value)) {
        throw new InvalidFloatException("Float out of range");
      }
      return value;
    } catch (NumberFormatException e) {
      throw new InvalidFloatException("Invalid float");
    }
  }
%}

/* Macros */
WhiteSpace = [ \t\r\n\f]+
/* Tokens */
Letter = [a-zA-Z]
Digit = [0-9]

ID = {Letter}({Letter}|{Digit})*

CTEINT = (0|[1-9]{Digit}*)

CTEFLOAT = ({Digit}+\.{Digit}+)|({Digit}+\.)|(\.{Digit}+)

QUOTE1 = \"
QUOTE2 = “
QUOTE3 = ”

CTESTR = ({QUOTE1}([^\"\r\n])*{QUOTE1})|({QUOTE2}([^”\r\n])*{QUOTE3})
/*CTESTR = \"([^\"\r\n])*\"*/

/* Caracteres válidos para validar dentro de comentarios: (letras, dígitos, espacios y los símbolos de nuestro lenguaje). */
CommentChar = [a-zA-Z0-9 \t\r\n!\"#+\-*/<>=:(),.{}“”]





%%



<YYINITIAL> {

  /* Keywords */
  /* whitespace */

  "#+" { commentDepth++; yybegin(COMMENT); }

  /* Constants */
  

  

  /* Reserved Words */
  "if"        { return symbol(sym.IF); }
  "else"      { return symbol(sym.ELSE); }
  "while"     { return symbol(sym.WHILE); }
  "read"      { return symbol(sym.READ); }
  "write"     { return symbol(sym.WRITE); }
  "init"      { return symbol(sym.INIT); }

  "Int"       { return symbol(sym.INT); }
  "Float"     { return symbol(sym.FLOAT); }
  "String"    { return symbol(sym.STRING); }

  "AND"       { return symbol(sym.AND); }
  "OR"        { return symbol(sym.OR); }
  "NOT"       { return symbol(sym.NOT); }

  "to"        { return symbol(sym.TO); }
  "step"      { return symbol(sym.STEP); }
  "next"      { return symbol(sym.NEXT); }
  "div"       { return symbol(sym.DIV); }
  "mod"       { return symbol(sym.MOD); }
  "for"       { return symbol(sym.FOR); }      

  /* Operators */
  -{Digit}+ {
    parseIntInRange(yytext());
    yypushback(yylength() - 1);
    return symbol(sym.SIMBOLO_MENOS);
  } 
  
  "!="        { return symbol(sym.SIMBOLO_DISTINTO); }
  "=="        { return symbol(sym.EQ); }
  "<="        { return symbol(sym.SIMBOLO_MENOR_O_IGUAL); }
  ">="        { return symbol(sym.SIMBOLO_MAYOR_O_IGUAL); }

  "+"         { return symbol(sym.SIMBOLO_MAS); }
  "-"         { return symbol(sym.SIMBOLO_MENOS); }
  "*"         { return symbol(sym.ASTERISCO); }
  "/"         { return symbol(sym.BARRA_DIAGONAL); }

  "<"         { return symbol(sym.SIMBOLO_MENOR); }
  ">"         { return symbol(sym.SIMBOLO_MAYOR); }

  ":="        { return symbol(sym.ASIG); }
  ":"         { return symbol(sym.DOS_PUNTOS); }

  "("         { return symbol(sym.PARENTESIS_IZQ); }
  ")"         { return symbol(sym.PARENTESIS_DER); }
  "{"         { return symbol(sym.LLAVE_IZQ); }
  "}"         { return symbol(sym.LLAVE_DER); }

  ","         { return symbol(sym.COMA); }

  /* Identifier */

  {CTEINT}    { return symbol(sym.CTE_INT, parseIntInRange(yytext())); }

  {CTEFLOAT}  { 
    float value = parseFloatInRange(yytext());
    SymbolTableEntry entry = new SymbolTableEntry("_"+yytext());
    entry.setType("CTE_FLOAT");
    entry.setValue(value);
    SymbolTable.add("_"+yytext(), entry);
    return symbol(sym.CTE_FLOAT, Double.valueOf(value)); }
  
  {CTESTR}    { String text = yytext();

    // sacar comillas (vale para " " y también “ ”)
    String content = text.substring(1, text.length() - 1);

    if (content.length() > 50) {
        throw new InvalidLengthException("String constant too long");
    }
    SymbolTableEntry entry = new SymbolTableEntry("_"+content);
    entry.setType("CTE_STR");
    entry.setValue(content);
    entry.setLength(content.length());
    SymbolTable.add("_"+content, entry);
    return symbol(sym.CTE_STR, content);}

  {ID}        {if (yytext().length() > 50) {  // probablemente 50, suele ser igual que string
        throw new InvalidLengthException("Identifier too long");
    }
    SymbolTable.add(yytext(), new SymbolTableEntry(yytext()));
    return symbol(sym.ID, yytext());}

  
  {WhiteSpace} {
    if (yytext().contains("\n")) System.out.println();
  }

}

<COMMENT> {
  /* La consigna dice "Deberán estar delimitados por #+ y +# y podrán estar anidados en un solo nivel.", acá lo implementamos */

  "#+"          { commentDepth++;
                  if (commentDepth > 2) {
                    throw new InvalidCommentException("Anidamiento de comentarios supera un solo nivel");
                  }
                }
                
  "+#"          { commentDepth--;
                  if (commentDepth == 0) { yybegin(YYINITIAL); }
                }
  {CommentChar} { /* carácter válido, se descarta */ }
  [^]           { throw new InvalidCommentException("Carácter inválido en comentario « " + yytext() + " »"); }
}

/* error fallback */
[^] { throw new UnknownCharacterException(yytext()); }
