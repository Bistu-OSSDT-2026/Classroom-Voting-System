@echo off
setlocal enabledelayedexpansion
title CVS Server

echo.
echo  ============================================
echo    CVS - Classroom Vote System
echo  ============================================
echo.

:: Ask user for JDK path
echo.
echo  Please enter your JDK 21 path, e.g.:
echo    C:\Program Files\Java\jdk-21
echo.
set /p JAVA_HOME="JDK path: "

if not exist "!JAVA_HOME!\bin\javac.exe" (
    echo.
    echo  Invalid JDK path (javac.exe not found).
    echo  Download Oracle JDK 21:
    echo  https://www.oracle.com/java/technologies/downloads/#jdk21-windows
    pause
    exit /b 1
)

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

set "PATH=!JAVA_HOME!\bin;%PATH%"
java -jar target\cvs-app.jar
pause
