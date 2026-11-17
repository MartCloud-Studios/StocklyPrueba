@echo off
setlocal

for %%I in ("%~dp0.") do set "ANDROID_PROJECT=%%~fI"
for %%I in ("%ANDROID_PROJECT%\..") do set "REPO_ROOT=%%~fI"
set "BACKEND_DIR=%REPO_ROOT%\StocklyBackend"

if not exist "%ANDROID_PROJECT%\gradlew.bat" (
    echo No se encontro gradlew.bat en "%ANDROID_PROJECT%".
    goto error
)

if not exist "%BACKEND_DIR%\mvnw.cmd" (
    echo No se encontro mvnw.cmd en "%BACKEND_DIR%".
    goto error
)

call "%ANDROID_PROJECT%\gradlew.bat" -p "%ANDROID_PROJECT%" :app:reverseBackendPort
if errorlevel 1 (
    echo No se pudo configurar adb reverse. Verifica que el dispositivo este conectado y autorizado.
    goto error
)

pushd "%BACKEND_DIR%"
call mvnw.cmd spring-boot:run
set "EXIT_CODE=%ERRORLEVEL%"
popd

pause
exit /b %EXIT_CODE%

:error
pause
exit /b 1