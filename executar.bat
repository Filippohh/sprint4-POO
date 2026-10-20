@echo off
setlocal
cd /d "%~dp0"

echo ===================================================================
echo   MOTIVA - Sprint 4 (Spring Boot + Spring Data JPA + API REST)
echo ===================================================================

REM 1. Carrega credenciais locais se o arquivo existir
if exist config.local.bat (
    echo [+] Carregando configuracoes de config.local.bat...
    call config.local.bat
) else (
    echo [!] AVISO: config.local.bat nao encontrado. Usando padroes do application.properties.
    echo     Copie config.local.exemplo.bat para config.local.bat para configurar o Oracle.
)

REM 2. Localiza o Maven
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set MVN_CMD=mvn
) else if exist "C:\ferramentas\apache-maven-3.9.6\bin\mvn.cmd" (
    set MVN_CMD="C:\ferramentas\apache-maven-3.9.6\bin\mvn.cmd"
) else (
    echo [ERRO] Maven nao encontrado no PATH nem em C:\ferramentas\apache-maven-3.9.6\bin.
    echo Instale o Maven ou adicione-o ao PATH do sistema.
    pause
    exit /b 1
)

echo [+] Executando a aplicacao com Spring Boot...
%MVN_CMD% spring-boot:run

endlocal
