# Pasos

1. Identificar tokens y detallarlos en en parser.cup como terminales
2. En lexer.flex detallar las expresiones regulares para cada token y las acciones léxicas luego de <YYINITIAL>  (si no aplica, directamente retornar el token)

## Duda 1

Identificando los tokens surgió un punto para preguntar en clase:
- Comparadores del tipo ">=" 

Opción 1:
Dos tokens por separado
= --> IGUAL 
> --> SIMBOLO_MAYOR

Opción 2 (LA QUE ELEGIMOS): 
Un token único
SIMBOLO_MAYOR_O_IGUAL --> >= 


Justificación: Se simplifica para escritura de las reglas y vemos que no se utiliza en ningún otro lado que no sea una comparación. Así que no habría complejidad

## Duda 2

Regex para validar comentarios.
Qué tenemos que tener en cuenta?
Debería aceptar un caracter chino dentro del comentario?

3. Fixeamos errores en nombre de clases

4. Agregamos tokens de temas especiales

## Observacion

Hay que ver la jerarquía de tokens.
Pusimos el "=" abajo de "==". Habría que ver que devuelve si lo ponemos al revés como para entenderla.

## Tabla de simbolos

1. Se creo la clase SymbolTable.java, la cual usa una coleccion de tipo Map para simular la tabla.
2. Se creo la clase SymbolTableEntry, la cual va a ser el Valor de cada entrada de la tabla, donde guardamos los atributos que creamos necesario, tales como el nombre, el valor, el tipo de dato, o lo que corresponda.
3. En SymbolTableGenerator.java, se agrega la logica para guardar en el archivo con el formato de tabla, todo el contenido de la misma.
4. En lexer.flex, se importan las clases anteriormente creadas, y se agrega la logica para agregar a la tabla de simbolos cuando se lea cualquier tipo de constante (string, float o int), y un ID.

TODO: debatir sobre que campos serian utiles agregar a la tabla de simbolos.

## Imprimir Tokens mientras parseamos

1. Se modifico los metodos "symbol", para que cuando lea un token, lo imprima del formato (NOMBRE_DE_TOKEN,LEXEMA).

Por ejemplo:

a := 3

(ID,a) (ASIG,;=) (CTE_INT,3)


## Modificaciones parser.cup (22/04/26)

- Cambié todos los no terminales a minusculas. Los no terminales quedan mejor en minusculas y los tokens en mayúsculas. 

- Se unificó asignación: 
asignacion ::= ID ASIG expresion
             | ID ASIG CTE_STR
             ;

- Cambié las reglas de condiciones por estas, que son como están en la teoría para que quede más legible.
Además agregué un NOT adelante de comparacion para que quede consistente y funcione tanto para el IF como para el WHILE.
Antes no funcionaba el NOT con el while.

seleccion ::=   IF PARENTESIS_IZQ condicion PARENTESIS_DER LLAVE_IZQ bloque LLAVE_DER ELSE LLAVE_IZQ bloque LLAVE_DER |
                IF PARENTESIS_IZQ condicion PARENTESIS_DER LLAVE_IZQ bloque LLAVE_DER

condicion ::= comparacion 
            | NOT comparacion
            | condicion AND comparacion 
            | condicion OR comparacion

comparacion ::= expresion comparador expresion

comparador ::= EQ
             | SIMBOLO_MENOR
             | SIMBOLO_MAYOR
             | SIMBOLO_MENOR_O_IGUAL
             | SIMBOLO_MAYOR_O_IGUAL
             ;



## Comparador distinto `!=`

- En lexer.flex: regla `"!="` que retorna `sym.SIMBOLO_DISTINTO`, junto a los otros comparadores de dos caracteres (`==`, `<=`, `>=`) para que el orden de las reglas sea el adecuado (similar a poner `"="` debajo de `"=="` en la jerarquía de tokens).
- En parser.cup: terminal `SIMBOLO_DISTINTO` y se lo agregó a `comparador` de esta forma:

comparador ::= EQ
             | SIMBOLO_DISTINTO
             | SIMBOLO_MENOR
             | SIMBOLO_MAYOR
             | SIMBOLO_MENOR_O_IGUAL
             | SIMBOLO_MAYOR_O_IGUAL
             ;



##Test de error
- Se agregan los siguientes test que prueban que errores en los temas especiales, en test.txt estan comentados, y se agregan en ParserTest.java como compilationError

stepError: El step no puede ser distinto a int
stepNegativeError: Los pasos no pueden ser negativos
divError: Deben ser dos enteros los que se dividen
toError: Falta de "to"
asignacionError: Falta de ":="
divisionByZeroError: No se puede dividir por cero, debe dar error
variableNextError: El lexema de id que se asigna en for id := cte, debe ser el mismo lexema que se usa en next