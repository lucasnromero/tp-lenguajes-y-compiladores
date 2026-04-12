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
