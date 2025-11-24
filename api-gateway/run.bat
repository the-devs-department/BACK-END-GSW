@echo off
REM Script para executar o API Gateway com Java 21 (temporário até instalar JDK 21)

echo ========================================
echo  API Gateway - Iniciando...
echo ========================================
echo.

REM Configurar JAVA_HOME para JDK 17
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

REM Verificar versão do Java
echo Versao do Java:
java -version
echo.

REM Executar o projeto
echo Iniciando API Gateway na porta 8086...
echo.
mvnw.cmd spring-boot:run

pause

