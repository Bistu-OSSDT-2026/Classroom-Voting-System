@echo off
setlocal enabledelayedexpansion
title CVS Server

:: === Auto-detect JDK 21+ ===
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        "%JAVA_HOME%\bin\java" -version 2>&1 | findstr "21\." >nul
        if !errorlevel! equ 0 goto :run
    )
)

:: Search common JDK 21+ locations
for %%d in (
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files\Java\jdk-17*"
    "C:\Program Files (x86)\Java\jdk-21*"
    "D:\cursor\jdk21"
    "D:\jdk\jdk-21*"
    "C:\jdk-21*"
) do (
    for /d %%j in (%%d) do (
        if exist "%%j\bin\java.exe" (
            set "JAVA_HOME=%%j"
            echo  Auto-detected JDK: %%j
            goto :run
        )
    )
)

echo  [ERROR] JDK 21+ not found!
echo  Current JAVA_HOME: %JAVA_HOME%
echo  Please install JDK 21 from: https://adoptium.net/download/
pause
exit /b 1

:run
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo  ============================================
echo    CVS - Classroom Vote System
echo    JAVA_HOME: %JAVA_HOME%
echo  ============================================
echo.

:: Check Java
java -version 2>&1 | findstr "version"
if %errorlevel% neq 0 (
    echo  Java not found! Install JDK 21:
    echo  https://adoptium.net/download/
    pause
    exit /b 1
)

:: Check if jar exists
if not exist "target\cvs-app.jar" (
    echo  No build found. Run build.bat first.
    pause
    exit /b 1
)

:: Kill any existing instance on port 8080
echo  Checking port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080.*LISTENING"') do (
    echo  Killing old process PID=%%a...
    taskkill /PID %%a /F >nul 2>&1
    timeout /t 2 /nobreak >nul
)

:: Run
echo.
echo  Starting: http://localhost:8080
echo  Teacher: teacher1 / 123456
echo  Student: student1 / 123456
echo  Press Ctrl+C to stop
echo  ============================================
echo.

java -jar target\cvs-app.jar
pause
endlocal
