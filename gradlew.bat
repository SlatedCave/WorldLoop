@echo off
setlocal
set "GRADLE_VERSION=9.8.2"
set "GRADLE_HOME=%USERPROFILE%\.gradle\worldloop-distributions\gradle-%GRADLE_VERSION%"
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
    echo Downloading Gradle %GRADLE_VERSION% from the official Gradle distribution server...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $v='%GRADLE_VERSION%'; $zip=Join-Path $env:TEMP ('gradle-' + $v + '-bin.zip'); $base=Join-Path $env:USERPROFILE '.gradle\worldloop-distributions'; New-Item -ItemType Directory -Force -Path $base | Out-Null; Invoke-WebRequest -Uri ('https://services.gradle.org/distributions/gradle-' + $v + '-bin.zip') -OutFile $zip; Expand-Archive -LiteralPath $zip -DestinationPath $base -Force; Remove-Item $zip -Force"
    if errorlevel 1 exit /b 1
)
call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
