@echo off
setlocal enabledelayedexpansion

:: O caractere '?' substitui o 'ã' para evitar problemas de codificacao no CMD
set "PROJ_DIR=C:\Vis?o_Futura\Projeto_Radar_Fornecedor\radar"

:: Testa a existencia acessando a pasta via CD
cd /d "%PROJ_DIR%" 2>nul
if not exist "mvnw.cmd" (
    powershell -Command "Write-Host '[ERRO] Nao foi possivel acessar a pasta ou encontrar o mvnw.cmd' -ForegroundColor Red"
    pause
    exit /b
)

:: Verifica se o processo java.exe ja esta ativo
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    powershell -Command "Stop-Process -Name 'java' -Force -ErrorAction SilentlyContinue"
    powershell -Command "Write-Host '[STATUS] Aplicacao PARADA com sucesso!' -ForegroundColor Red"
    timeout /t 3 >nul
    exit /b
)

:: Executa a aplicacao apontando para o diretorio resolvido
powershell -Command "Start-Process cmd.exe -ArgumentList '/c cd /d \"%CD%\" && .\mvnw.cmd spring-boot:run > spring_boot.log 2>&1' -WindowStyle Hidden"

timeout /t 5 >nul

tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    powershell -Command "Write-Host '[STATUS] Aplicacao INICIADA em segundo plano!' -ForegroundColor Green"
) else (
    powershell -Command "Write-Host '[ERRO] Falha ao iniciar! Verifique o log em: spring_boot.log' -ForegroundColor Red"
)

echo.
timeout /t 4
exit /b