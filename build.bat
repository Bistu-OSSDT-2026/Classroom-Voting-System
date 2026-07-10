@echo off
setlocal enabledelayedexpansion
title CVS Build

echo.
echo  Building CVS...

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

set "PATH=!JAVA_HOME!\bin;%PATH%"
call mvnw.cmd package -DskipTests -q
if %errorlevel% equ 0 (
    echo.
    echo  Build success! Now double-click start.bat
) else (
    echo.
    echo  Build FAILED.
)
pause
