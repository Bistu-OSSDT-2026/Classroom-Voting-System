@echo off
cd /d "%~dp0"
title CVS - Classroom Vote System

echo ============================================
echo   Classroom Vote System - Starting...
echo ============================================
echo.

:: Step 1: Check Java
echo [1/3] Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found! Please install JDK 21
    echo Download: https://adoptium.net/download/
    echo.
    pause
    exit /b
)
echo        Java OK.
echo.

:: Step 2: Build if needed
if not exist "target\cvs-app.jar" (
    echo [2/3] First run - building project (download dependencies, 1-2 min)...
    call mvnw.cmd clean package -DskipTests -q
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed! Check your internet connection.
        echo.
        pause
        exit /b
    )
    echo        Build complete!
) else (
    echo [2/3] Using existing build.
)
echo.

:: Step 3: Start server
echo [3/3] Starting server at http://localhost:8080
echo.
echo   Default accounts:
echo     Teacher: teacher1 / 123456
echo     Student: student1 / 123456
echo.
echo   Press Ctrl+C to stop
echo ============================================
echo.

java -jar target\cvs-app.jar

echo.
echo Server stopped.
pause
