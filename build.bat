@echo off
setlocal enabledelayedexpansion

:: === Auto-detect JDK 21+ ===
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" goto :build
)

:: Search common JDK locations
set "JDK_FOUND="
for %%d in (
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files (x86)\Java\jdk-21*"
    "D:\cursor\jdk21"
    "D:\jdk\jdk-21*"
    "C:\jdk-21*"
) do (
    for /d %%j in (%%d) do (
        if exist "%%j\bin\javac.exe" (
            set "JAVA_HOME=%%j"
            set "JDK_FOUND=1"
            goto :found
        )
    )
)

:: Fallback: use java on PATH
for /f "tokens=*" %%i in ('where javac 2^>nul') do (
    set "JDK_FOUND=1"
    goto :build
)

echo  [ERROR] JDK 21+ not found!
echo  Please install JDK 21 from: https://adoptium.net/download/
echo  Or set JAVA_HOME manually to your JDK 21 installation path.
pause
exit /b 1

:found
echo  Auto-detected JDK: %JAVA_HOME%

:build
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo  ============================================
echo    Building CVS... (first time takes 1-2 min to download)
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
