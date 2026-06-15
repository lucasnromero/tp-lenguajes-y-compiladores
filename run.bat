@echo off

echo "Compiling run.txt"
java "-jar" "target\lyc-compiler-2.0.0.jar" "target\input\test.txt"
COPY  "target\output\final.asm" "target\asm\final.asm"



echo Compiling test.txt
java -jar target\lyc-compiler-2.0.0.jar target\input\test.txt

copy target\output\final.asm target\asm\final.asm

DOSBox-0.74-3\DOSBox.exe ^
-c "mount c \"%CD%\target\asm\"" ^
-c "c:" ^
-c "run.bat" ^
-c "exit"