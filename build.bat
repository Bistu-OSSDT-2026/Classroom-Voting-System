@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo.
echo  Building CVS... (first time takes 1-2 min to download)
echo.
call mvnw.cmd package -DskipTests
if %errorlevel% equ 0 (
    echo.
    echo  Build success! Now double-click start.bat
) else (
    echo.
    echo  Build FAILED. Check your internet.
)
pause
