@echo off
setlocal enabledelayedexpansion

:: === Auto-detect JDK 21+ ===
:: First check if current JAVA_HOME points to JDK 21+
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        "%JAVA_HOME%\bin\javac" -version 2>&1 | findstr "21\." >nul
        if !errorlevel! equ 0 goto :build
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
        if exist "%%j\bin\javac.exe" (
            set "JAVA_HOME=%%j"
            echo  Auto-detected JDK: %%j
            goto :build
        )
    )
)

echo  [ERROR] JDK 21+ not found!
echo  Current JAVA_HOME: %JAVA_HOME%
echo  Please install JDK 21 from: https://adoptium.net/download/
pause
exit /b 1

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
