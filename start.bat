@echo off
setlocal enabledelayedexpansion
title CVS Server

echo.
echo  ============================================
echo    CVS - Classroom Vote System
echo  ============================================
echo.

:: ===== JDK 查找 =====
call :find_jdk
if errorlevel 1 (
    echo.
    echo  ============================================
    echo   JDK 21+ not found!
    echo  ============================================
    echo.
    choice /c YNM /m "[Y] Auto-download JDK  [N] Enter path manually  [M] Exit"
    if errorlevel 3 exit /b 1
    if errorlevel 2 goto :manual_path
    if errorlevel 1 (
        call :download_jdk
        if errorlevel 1 goto :manual_path
        goto :run
    )
)

:found_java
:: JDK 已就绪
goto :run

:: ===== 手动输入路径 =====
:manual_path
echo.
echo  Enter your JDK path, e.g.: C:\Program Files\Java\jdk-21
set /p USER_PATH="JDK path: "
if exist "!USER_PATH!\bin\javac.exe" (
    set "JAVA_HOME=!USER_PATH!"
    goto :run
)
if exist "!USER_PATH!\javac.exe" (
    set "JAVA_HOME=!USER_PATH!"
    goto :run
)
echo  Invalid JDK path (javac not found).
exit /b 1

:: ===== 启动应用 =====
:run
if not exist "target\cvs-app.jar" (
    echo.
    echo  No build found. Run build.bat first.
    pause
    exit /b 1
)

:: Kill existing instance
echo.
echo  Checking port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080.*LISTENING"') do (
    echo  Killing old process PID=%%a...
    taskkill /PID %%a /F >nul 2>&1
    timeout /t 2 /nobreak >nul
)

echo.
echo  Starting: http://localhost:8080
echo  Teacher: teacher1 / 123456
echo  Student: student1 / 123456
echo  Press Ctrl+C to stop
echo  ============================================
echo.

if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
java -jar target\cvs-app.jar
pause
exit /b 0

:: ===== JDK 查找函数 =====
:find_jdk
set "JAVA_HOME_FOUND="

:: 1) 系统 JAVA_HOME
if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\javac.exe" (
        set "JAVA_HOME_FOUND=1"
        exit /b 0
    )
)

:: 2) PATH 中查找 javac
where javac >nul 2>&1
if %errorlevel% equ 0 exit /b 0

:: 3) 常见路径
for /d %%d in (
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files (x86)\Java\jdk-21*"
) do (
    if exist "%%d\bin\javac.exe" (
        set "JAVA_HOME=%%d"
        exit /b 0
    )
)

exit /b 1

:: ===== 自动下载 JDK 21 =====
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

for /d %%d in ("%JDK_DIR%\jdk-21*") do (
    set "JAVA_HOME=%%d"
)

del "%JDK_ZIP%" 2>nul

if not defined JAVA_HOME (
    echo  Extraction failed.
    exit /b 1
)

echo  JDK 21 installed to: !JAVA_HOME!
setx JAVA_HOME "!JAVA_HOME!" >nul 2>&1
exit /b 0
