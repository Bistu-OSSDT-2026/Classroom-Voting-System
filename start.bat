@echo off
setlocal enabledelayedexpansion
title CVS Server

echo.
echo  ============================================
echo    CVS - Classroom Vote System
echo  ============================================
echo.

:: Find JDK
call :find_jdk
if errorlevel 1 goto :no_jdk

:run
if not exist "target\cvs-app.jar" (
    echo.
    echo  No build found. Run build.bat first.
    pause
    exit /b 1
)

:: Kill existing instance on port 8080
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

:no_jdk
echo.
echo  ============================================
echo   JDK 21+ not found! (need javac, not JRE)
echo  ============================================
echo.
echo  Enter your JDK path, e.g.:
echo    C:\Program Files\Java\jdk-21
echo.
set /p USER_PATH="JDK path: "

if exist "!USER_PATH!\bin\javac.exe" (
    set "JAVA_HOME=!USER_PATH!"
    goto :run
)
if exist "!USER_PATH!\javac.exe" (
    set "JAVA_HOME=!USER_PATH!"
    goto :run
)

echo.
echo  Invalid path. Download Oracle JDK 21:
echo  https://www.oracle.com/java/technologies/downloads/#jdk21-windows
pause
exit /b 1

:find_jdk
if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\javac.exe" exit /b 0
)
where javac >nul 2>&1
if %errorlevel% equ 0 (
    for /f "delims=" %%i in ('where javac') do (
        pushd "%%~dpi.."
        set "JAVA_HOME=!CD!"
        popd
        exit /b 0
    )
)
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
