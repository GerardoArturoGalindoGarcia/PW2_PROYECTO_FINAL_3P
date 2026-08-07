@echo off
REM Script para compilar y construir el Sistema de Facturación
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║   Sistema de Facturación con CAI/RTN - Build Script       ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

cd /d %~dp0sistemafacturacion

REM Verificar si Maven está instalado
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no está instalado o no está en el PATH
    echo Descárgalo desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [1/4] Limpiando compilaciones anteriores...
call mvn clean
if errorlevel 1 goto error

echo.
echo [2/4] Descargando dependencias...
call mvn dependency:resolve
if errorlevel 1 goto error

echo.
echo [3/4] Compilando el proyecto...
call mvn compile
if errorlevel 1 goto error

echo.
echo [4/4] Construyendo el archivo WAR...
call mvn install
if errorlevel 1 goto error

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║              BUILD COMPLETADO EXITOSAMENTE                ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo El archivo WAR se encuentra en: %CD%\target\sistemafacturacion.war
echo.
echo Próximos pasos:
echo 1. Copiar el WAR a tu servidor de aplicaciones
echo 2. Reiniciar el servidor
echo 3. Acceder a: http://localhost:8080/sistemafacturacion/
echo 4. Usar credenciales: admin / admin123
echo.
pause
exit /b 0

:error
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                  ERROR EN LA COMPILACION                   ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
pause
exit /b 1
