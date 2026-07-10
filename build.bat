@echo off
title CVS Build

echo.
echo  Building CVS... (first time takes 1-2 min to download)
echo.

:: mvnw.cmd 自带 Java 查找逻辑，无需设置 JAVA_HOME
call mvnw.cmd package -DskipTests
if %errorlevel% equ 0 (
    echo.
    echo  Build success! Now double-click start.bat
) else (
    echo.
    echo  Build FAILED. Make sure Oracle JDK 21+ is installed.
    echo  Download: https://www.oracle.com/java/technologies/downloads/#jdk21-windows
)
pause
