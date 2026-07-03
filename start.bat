@echo off
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
    echo  No build found. Run this in terminal first:
    echo    mvnw.cmd package -DskipTests
    echo.
    pause
    exit
)

:: Run
echo  Starting: http://localhost:8080
echo  Teacher: teacher1 / 123456
echo  Student: student1 / 123456
echo  Press Ctrl+C to stop
echo  ============================================
echo.

java -jar target\cvs-app.jar
pause
