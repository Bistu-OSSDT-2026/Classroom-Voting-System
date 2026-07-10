@echo off
setlocal enabledelayedexpansion
title CVS Build

echo.
echo  Building CVS...

:: Check for JDK (javac must exist, not just java)
call :find_jdk
if errorlevel 1 (
    echo.
    echo  ============================================
    echo   JDK 21+ not found! (JRE is not enough - need javac compiler)
    echo  ============================================
    echo.
    choice /c YN /m "Download JDK 21 automatically"
    if errorlevel 2 goto :nojava
    call :download_jdk
    if errorlevel 1 goto :nojava
    goto :build
)

:build
call mvnw.cmd package -DskipTests -q
if %errorlevel% equ 0 (
    echo.
    echo  Build success! Now double-click start.bat
) else (
    echo.
    echo  Build FAILED.
)
pause
exit /b 0

:nojava
echo.
echo  Please install Oracle JDK 21 manually:
echo  https://www.oracle.com/java/technologies/downloads/#jdk21-windows
pause
exit /b 1

:: ===== JDK 查找函数 =====
:find_jdk
set "JAVA_CMD="

:: 1) 系统 JAVA_HOME
if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\javac.exe" (
        set "JAVA_CMD=!JAVA_HOME!\bin\java.exe"
        goto :jdk_ok
    )
)

:: 2) PATH 中查找
where javac >nul 2>&1
if %errorlevel% equ 0 goto :jdk_ok

:: 3) 常见路径
for /d %%d in (
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files (x86)\Java\jdk-21*"
) do (
    if exist "%%d\bin\javac.exe" (
        set "JAVA_CMD=%%d\bin\java.exe"
        set "JAVA_HOME=%%d"
        goto :jdk_ok
    )
)
exit /b 1

:jdk_ok
echo  JDK found!
exit /b 0

:: ===== 自动下载 JDK =====
:download_jdk
set "JDK_URL=https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.zip"
set "JDK_DIR=%~dp0jdk"
set "JDK_ZIP=%TEMP%\jdk21.zip"

echo.
echo  Downloading JDK 21 (~180MB)...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%JDK_URL%' -OutFile '%JDK_ZIP%'" 2>&1
if not exist "%JDK_ZIP%" (
    echo  Download failed.
    exit /b 1
)

echo  Extracting...
rmdir /s /q "%JDK_DIR%" 2>nul
powershell -Command "Expand-Archive -Path '%JDK_ZIP%' -DestinationPath '%JDK_DIR%'" 2>&1

:: Find the actual JDK folder inside
for /d %%d in ("%JDK_DIR%\jdk-21*") do (
    set "JAVA_HOME=%%d"
    set "JAVA_CMD=%%d\bin\java.exe"
)

del "%JDK_ZIP%" 2>nul

if not defined JAVA_HOME (
    echo  Extraction failed.
    exit /b 1
)

echo  JDK 21 installed to: !JAVA_HOME!
setx JAVA_HOME "!JAVA_HOME!" >nul 2>&1
exit /b 0
