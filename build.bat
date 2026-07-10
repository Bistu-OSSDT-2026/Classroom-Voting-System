@echo off
setlocal enabledelayedexpansion
title CVS Build

echo.
echo  Building CVS...

:: Find JDK (must have javac)
call :find_jdk
if errorlevel 1 goto :no_jdk

:build
call mvnw.cmd package -DskipTests -q
if %errorlevel% equ 0 (
    echo.
    echo  Build success! Now double-click start.bat
) else (
    echo.
    echo  Build FAILED.
)
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
    goto :build
)
if exist "!USER_PATH!\javac.exe" (
    set "JAVA_HOME=!USER_PATH!"
    goto :build
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
if %errorlevel% equ 0 exit /b 0
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
