@echo off
title CVS Server

echo.
echo  ============================================
echo    CVS - Classroom Vote System
echo  ============================================
echo.

:: 自动查找 Java（优先级：JAVA_HOME > PATH > 常见安装目录）
set "JAVA_CMD=java"

:: 1) 尝试系统 JAVA_HOME 环境变量
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        goto :found_java
    )
)

:: 2) 尝试 PATH 中是否有 java
where java >nul 2>&1
if %errorlevel% equ 0 (
    goto :found_java
)

:: 3) 搜索 Oracle JDK 21 安装路径
for /d %%d in (
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files (x86)\Java\jdk-21*"
) do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_CMD=%%d\bin\java.exe"
        set "JAVA_HOME=%%d"
        goto :found_java
    )
)

:: 找不到 Java
echo  Java not found! Please install Oracle JDK 21:
echo  https://www.oracle.com/java/technologies/downloads/#jdk21-windows
pause
exit /b 1

:found_java
echo  Java found!

:: Check Java version
"%JAVA_CMD%" -version 2>&1 | findstr /i "version"
if %errorlevel% neq 0 (
    echo  Error checking Java version
)

:: Check if jar exists
if not exist "target\cvs-app.jar" (
    echo.
    echo  No build found. Run build.bat first.
    pause
    exit /b 1
)

:: Kill any existing instance on port 8080
echo.
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

"%JAVA_CMD%" -jar target\cvs-app.jar
pause
