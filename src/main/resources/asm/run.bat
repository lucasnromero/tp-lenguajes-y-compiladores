@echo off

SET PATH=%PATH%;%CD%\TASM

tasm numbers.asm
tasm final.asm
tlink final.obj numbers.obj

pause

final.exe

pause