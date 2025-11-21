@echo off
REM Script de verificacao e inicializacao do MongoDB

echo ========================================
echo  MongoDB - Verificacao e Inicializacao
echo ========================================
echo.

REM Verificar se o MongoDB esta instalado
echo [1/3] Verificando instalacao do MongoDB...
sc query MongoDB >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] MongoDB instalado como servico!
    echo.

    REM Verificar se esta rodando
    echo [2/3] Verificando status do servico...
    sc query MongoDB | find "RUNNING" >nul
    if %errorlevel% == 0 (
        echo [OK] MongoDB ja esta rodando!
    ) else (
        echo [AVISO] MongoDB instalado mas nao esta rodando.
        echo Tentando iniciar o servico...
        net start MongoDB
        if %errorlevel% == 0 (
            echo [OK] MongoDB iniciado com sucesso!
        ) else (
            echo [ERRO] Falha ao iniciar MongoDB. Execute este script como Administrador.
            pause
            exit /b 1
        )
    )
) else (
    echo [ERRO] MongoDB nao esta instalado como servico!
    echo.
    echo OPCOES DE INSTALACAO:
    echo.
    echo 1. MongoDB Community Server (Recomendado):
    echo    https://www.mongodb.com/try/download/community
    echo.
    echo 2. MongoDB via Docker:
    echo    docker run -d -p 27017:27017 --name mongodb mongo:latest
    echo.
    echo 3. MongoDB Atlas (Cloud):
    echo    https://www.mongodb.com/cloud/atlas/register
    echo.
    echo Apos instalar, execute este script novamente.
    pause
    exit /b 1
)

echo.
echo [3/3] Testando conexao com MongoDB...
echo db.version() | mongosh --quiet >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] MongoDB acessivel e respondendo!
    echo.
    echo ========================================
    echo  MongoDB PRONTO para uso!
    echo ========================================
    echo.
    echo Porta: 27017
    echo Database: api-gateway
    echo.
    echo Agora voce pode executar run.bat
    echo.
) else (
    echo [AVISO] MongoDB esta rodando mas mongosh nao esta instalado.
    echo Isso nao impede o funcionamento do projeto.
    echo.
    echo Para instalar mongosh:
    echo https://www.mongodb.com/try/download/shell
    echo.
)

pause

