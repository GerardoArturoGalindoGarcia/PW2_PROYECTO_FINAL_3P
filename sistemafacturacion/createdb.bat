@echo off
cd /d "C:\Users\WilliamB\Documents\GitHub\PW2_PROYECTO_FINAL_3P\sistemafacturacion"

set CLASSPATH=.;lib\sqlite-jdbc-3.44.0.0.jar;lib\slf4j-api-2.0.9.jar;lib\slf4j-nop-2.0.9.jar

echo Inicializando base de datos SQLite...
echo Classpath: %CLASSPATH%
echo.

java -cp "%CLASSPATH%" InitializeDatabase

echo.
echo Proceso completado.
pause
