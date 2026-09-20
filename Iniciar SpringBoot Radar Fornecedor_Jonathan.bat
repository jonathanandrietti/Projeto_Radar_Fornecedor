@echo off
setlocal enabledelayedexpansion

:: Forca uso do JDK 11 (instalado no sistema)
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.28.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: O caractere '?' substitui o 'a com til' para evitar problemas de codificacao no CMD
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
    powershell -Command "Write-Host '[STATUS] Finalizando o Spring Boot!' -ForegroundColor Red"
    timeout /t 3 >nul
    exit /b
)

:: Gera script auxiliar com os caminhos ja resolvidos
set "LAUNCHER=%TEMP%\radar_launch.bat"
(
    echo @echo off
    echo set "JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.28.6-hotspot"
    echo set "PATH=C:\Program Files\Microsoft\jdk-11.0.28.6-hotspot\bin;%%PATH%%"
    echo cd /d "%CD%"
    echo call mvnw.cmd spring-boot:run ^> spring_boot.log 2^>^&1
) > "%LAUNCHER%"

:: Executa em janela oculta usando o script auxiliar
powershell -Command "Start-Process cmd.exe -ArgumentList '/c \"%LAUNCHER%\"' -WindowStyle Hidden"

timeout /t 5 >nul

tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    powershell -Command "Write-Host '[STATUS] Iniciando o Spring Boot!' -ForegroundColor Green"
) else (
    powershell -Command "Write-Host '[ERRO] Falha ao iniciar! Verifique o log em: spring_boot.log' -ForegroundColor Red"
)

echo.
timeout /t 4
exit /b

