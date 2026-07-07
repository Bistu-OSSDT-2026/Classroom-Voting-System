@echo off
set JAVA_HOME=C:\大学\暑期开源软件\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
title CVS Server

echo.
echo  ============================================
echo    CVS - Classroom Vote System
echo  ============================================
echo.

:: Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  Java not found! Install JDK 21:
    echo  https://adoptium.net/download/
    pause
    exit
)

:: Check if jar exists
if not exist "target\cvs-app.jar" (
    echo  No build found. Run build.bat first.
    pause
    exit
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
