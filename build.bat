@echo off
setlocal enabledelayedexpansion

:: === Auto-detect JDK 21+ ===
:: Step 1: Check current JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        "%JAVA_HOME%\bin\javac.exe" -version 2>&1 | findstr "21\." >nul
        if !errorlevel! equ 0 goto :build
    )
)

:: Step 2: Search common directories (using dir to expand wildcards)
set "JDK_DIR="
for /f "tokens=*" %%i in ('dir /b /ad "C:\Program Files\Java\jdk-21*" 2^>nul') do (
    set "JDK_DIR=C:\Program Files\Java\%%i"
    goto :found
)
for /f "tokens=*" %%i in ('dir /b /ad "C:\Program Files\Java\jdk-17*" 2^>nul') do (
    set "JDK_DIR=C:\Program Files\Java\%%i"
    goto :found
)
for /f "tokens=*" %%i in ('dir /b /ad "D:\cursor\jdk*" 2^>nul') do (
    set "JDK_DIR=D:\cursor\%%i"
    goto :found
)
for /f "tokens=*" %%i in ('dir /b /ad "D:\jdk\jdk-21*" 2^>nul') do (
    set "JDK_DIR=D:\jdk\%%i"
    goto :found
)

:: Step 3: Last resort - check common fixed paths
for %%d in (
    "C:\Program Files\Java\jdk-21.0.10"
    "D:\cursor\jdk21"
) do (
    if exist "%%~d\bin\javac.exe" (
        set "JDK_DIR=%%~d"
        goto :found
    )
)

echo  [ERROR] JDK 21+ not found!
echo  Current JAVA_HOME: %JAVA_HOME%
echo  Please install JDK 21 from: https://adoptium.net/download/
pause
exit /b 1

:found
set "JAVA_HOME=%JDK_DIR%"
echo  Auto-detected JDK: %JAVA_HOME%

:build
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo  ============================================
echo    Building CVS... (first time 1-2 min)
echo    JAVA_HOME: %JAVA_HOME%
echo  ============================================
echo.

call mvnw.cmd package -DskipTests
if %errorlevel% equ 0 (
    echo.
    echo  ============================================
    echo    Build success! Now double-click start.bat
    echo  ============================================
) else (
    echo.
    echo  ============================================
    echo    Build FAILED. Check your internet.
    echo  ============================================
)
pause
endlocal
