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
    exit /b 1
)

:: Verifica se o processo java.exe ja esta ativo
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    powershell -Command "Write-Host '[STATUS] Finalizando o Spring Boot!' -ForegroundColor Red"
    
    :: Para Java
    taskkill /IM java.exe /F 2>nul
    
    :: Fecha apenas a aba (Ctrl+W) do navegador - nao fecha o navegador inteiro
    timeout /t 1 >nul
    powershell -Command "Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.SendKeys]::SendWait('%%^w')" -ErrorAction SilentlyContinue
    
    timeout /t 2 >nul
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
    
    :: Aguarda 20 segundos com contagem regressiva
    powershell -Command "Write-Host '[AGUARDANDO] Servidor inicializando...' -ForegroundColor Yellow"
    
    for /L %%i in (20,-1,1) do (
        if %%i equ 20 (
            powershell -Command "Write-Host '  20 segundos restantes...' -ForegroundColor Cyan"
        )
        if %%i equ 15 (
            cls
            powershell -Command "Write-Host '[STATUS] Iniciando o Spring Boot!' -ForegroundColor Green"
            powershell -Command "Write-Host '[AGUARDANDO] Servidor inicializando...' -ForegroundColor Yellow"
            powershell -Command "Write-Host '  15 segundos restantes...' -ForegroundColor Cyan"
        )
        if %%i equ 10 (
            cls
            powershell -Command "Write-Host '[STATUS] Iniciando o Spring Boot!' -ForegroundColor Green"
            powershell -Command "Write-Host '[AGUARDANDO] Servidor inicializando...' -ForegroundColor Yellow"
            powershell -Command "Write-Host '  10 segundos restantes...' -ForegroundColor Cyan"
        )
        if %%i equ 5 (
            cls
            powershell -Command "Write-Host '[STATUS] Iniciando o Spring Boot!' -ForegroundColor Green"
            powershell -Command "Write-Host '[AGUARDANDO] Servidor inicializando...' -ForegroundColor Yellow"
            powershell -Command "Write-Host '  5 segundos restantes... [ABRINDO NAVEGADOR EM BREVE]' -ForegroundColor Cyan"
        )
        timeout /t 1 >nul
    )
    
    cls
    powershell -Command "Write-Host '[STATUS] Iniciando o Spring Boot!' -ForegroundColor Green"
    powershell -Command "Write-Host '[ABRINDO] Navegador em http://localhost:8080' -ForegroundColor Cyan"
    
    :: Abre o navegador automaticamente na pagina de login
    start http://localhost:8080/login.html
    
    timeout /t 3 >nul
    cls
    powershell -Command "Write-Host '[SUCESSO] Sistema pronto! Navegador aberto.' -ForegroundColor Green"
    powershell -Command "Write-Host '[CACHE] Se nao carregar, PRESSIONE: Ctrl+Shift+R (Hard Refresh)' -ForegroundColor Yellow"
    powershell -Command "Write-Host '[ENCERRAR] Feche esta janela para parar o servidor e fechar a aba' -ForegroundColor Yellow"
    
    :: Aguarda o usuario fechar a janela
    pause
    
    :: Quando usuario fecha, para Java
    powershell -Command "Write-Host '[FINALIZANDO] Parando servidor...' -ForegroundColor Red"
    taskkill /IM java.exe /F 2>nul
    
    :: Fecha apenas a aba (Ctrl+W)
    timeout /t 1 >nul
    powershell -Command "Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.SendKeys]::SendWait('%%^w')" -ErrorAction SilentlyContinue
    
    timeout /t 1 >nul
    exit /b
    
) else (
    powershell -Command "Write-Host '[ERRO] Falha ao iniciar! Verifique o log em: spring_boot.log' -ForegroundColor Red"
)

echo.
timeout /t 2
exit /b


