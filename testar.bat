@echo off
setlocal
cd /d "%~dp0"

echo ===================================================================
echo   MOTIVA - Executando Testes Automatizados (MockMvc + SpringBootTest)
echo ===================================================================

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set MVN_CMD=mvn
) else if exist "C:\ferramentas\apache-maven-3.9.6\bin\mvn.cmd" (
    set MVN_CMD="C:\ferramentas\apache-maven-3.9.6\bin\mvn.cmd"
) else (
    echo [ERRO] Maven nao encontrado.
    exit /b 1
)

%MVN_CMD% test

endlocal
