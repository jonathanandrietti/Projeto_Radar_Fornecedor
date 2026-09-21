@echo off
setlocal enabledelayedexpansion

:: Forca uso do JDK 24 (compativel com Java 17 target do projeto)
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
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
    powershell -Command "Write-Host '[STATUS] Aplicacao PARADA com sucesso!' -ForegroundColor Red"
    timeout /t 3 >nul
    exit /b
)

:: =================================================================
:: COPIAR ARQUIVOS ATUALIZADOS PARA target/classes
:: =================================================================
powershell -Command "Write-Host '[SINCRONIZACAO] Copiando arquivos JS e HTML atualizados...' -ForegroundColor Yellow"
powershell -Command "Copy-Item -Path 'src\main\resources\static\js\preenchimento-automatico.js' -Destination 'target\classes\static\js\preenchimento-automatico.js' -Force -ErrorAction SilentlyContinue"
powershell -Command "Copy-Item -Path 'src\main\resources\static\script.js' -Destination 'target\classes\static\script.js' -Force -ErrorAction SilentlyContinue"
powershell -Command "Copy-Item -Path 'src\main\resources\static\pages\*.html' -Destination 'target\classes\static\pages\' -Force -ErrorAction SilentlyContinue"
powershell -Command "Copy-Item -Path 'src\main\resources\static\style.css' -Destination 'target\classes\static\style.css' -Force -ErrorAction SilentlyContinue"
powershell -Command "Write-Host '[SINCRONIZACAO] Arquivos sincronizados com sucesso!' -ForegroundColor Green"

:: =================================================================
:: LIMPAR CACHE DO NAVEGADOR (Chrome, Edge, Firefox)
:: =================================================================
powershell -Command "Write-Host '[LIMPEZA] Limpando cache dos navegadores...' -ForegroundColor Yellow"

:: Chrome
powershell -Command "Remove-Item -Path '%USERPROFILE%\AppData\Local\Google\Chrome\User Data\Default\Cache' -Recurse -Force -ErrorAction SilentlyContinue"
powershell -Command "Remove-Item -Path '%USERPROFILE%\AppData\Local\Google\Chrome\User Data\Default\Code Cache' -Recurse -Force -ErrorAction SilentlyContinue"

:: Edge
powershell -Command "Remove-Item -Path '%USERPROFILE%\AppData\Local\Microsoft\Edge\User Data\Default\Cache' -Recurse -Force -ErrorAction SilentlyContinue"
powershell -Command "Remove-Item -Path '%USERPROFILE%\AppData\Local\Microsoft\Edge\User Data\Default\Code Cache' -Recurse -Force -ErrorAction SilentlyContinue"

:: Firefox
powershell -Command "Remove-Item -Path '%USERPROFILE%\AppData\Local\Mozilla\Firefox\Profiles\*.default-release\cache2' -Recurse -Force -ErrorAction SilentlyContinue"

powershell -Command "Write-Host '[LIMPEZA] Cache dos navegadores limpo!' -ForegroundColor Green"

:: Gera script auxiliar com os caminhos ja resolvidos
set "LAUNCHER=%TEMP%\radar_launch.bat"
(
    echo @echo off
    echo set "JAVA_HOME=C:\Program Files\Java\jdk-24"
    echo set "PATH=C:\Program Files\Java\jdk-24\bin;%%PATH%%"
    echo cd /d "%CD%"
    echo call mvnw.cmd spring-boot:run ^> spring_boot.log 2^>^&1
) > "%LAUNCHER%"

:: Executa em janela oculta usando o script auxiliar
powershell -Command "Start-Process cmd.exe -ArgumentList '/c \"%LAUNCHER%\"' -WindowStyle Hidden"

timeout /t 5 >nul

tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    powershell -Command "Write-Host '[STATUS] Aplicacao INICIADA em segundo plano!' -ForegroundColor Green"
    powershell -Command "Write-Host '[IMPORTANTE] Abra o navegador e pressione Ctrl+Shift+R para hard refresh!' -ForegroundColor Cyan"
) else (
    powershell -Command "Write-Host '[ERRO] Falha ao iniciar! Verifique o log em: spring_boot.log' -ForegroundColor Red"
)

echo.
timeout /t 4
exit /b
