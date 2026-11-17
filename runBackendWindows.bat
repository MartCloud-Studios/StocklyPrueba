@echo off
setlocal

for %%I in ("%~dp0.") do set "REPO_ROOT=%%~fI"
set "ANDROID_PROJECT=%REPO_ROOT%\stocklyApp"
set "BACKEND_DIR=%REPO_ROOT%\StocklyBackend"
set "JWT_SECRET_FILE=%LOCALAPPDATA%\Stockly\jwt-secret.txt"

if not exist "%BACKEND_DIR%\mvnw.cmd" (
    echo No se encontro mvnw.cmd en "%BACKEND_DIR%".
    goto error
)

if not defined JWT_SECRET (
    if not exist "%JWT_SECRET_FILE%" (
        if not exist "%LOCALAPPDATA%\Stockly" mkdir "%LOCALAPPDATA%\Stockly"
        powershell -NoProfile -Command "$bytes = New-Object byte[] 32; $rng = [Security.Cryptography.RandomNumberGenerator]::Create(); $rng.GetBytes($bytes); [Convert]::ToBase64String($bytes) | Set-Content -NoNewline -Encoding ascii '%JWT_SECRET_FILE%'"
        if errorlevel 1 (
            echo No se pudo generar la clave JWT.
            goto error
        )
    )
    set /p "JWT_SECRET="<"%JWT_SECRET_FILE%"
)

if not defined JWT_SECRET (
    echo No se pudo cargar la clave JWT guardada en "%JWT_SECRET_FILE%".
    goto error
)

if defined ANDROID_HOME goto reverse_backend_port
if defined ANDROID_SDK_ROOT goto reverse_backend_port
if exist "%ANDROID_PROJECT%\local.properties" goto reverse_backend_port
echo Android SDK no configurado; se omitira adb reverse.
goto start_backend

:reverse_backend_port
if exist "%ANDROID_PROJECT%\gradlew.bat" (
    call "%ANDROID_PROJECT%\gradlew.bat" -p "%ANDROID_PROJECT%" :app:reverseBackendPort
    if errorlevel 1 echo No se pudo configurar adb reverse; el backend se iniciara de todos modos.
)

:start_backend
if not exist "%ANDROID_PROJECT%\gradlew.bat" (
    echo No se encontro gradlew.bat; se iniciara solo el backend.
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