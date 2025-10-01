@echo off
echo ================================================
echo    Microbot Update Script
echo ================================================
echo.

echo [1/2] Pulling latest changes from GitHub...
git pull origin main

if %errorlevel% neq 0 (
    echo ERROR: Git pull failed!
    pause
    exit /b 1
)

echo.
echo [2/2] Building Microbot...
mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo ================================================
echo    UPDATE COMPLETE!
echo ================================================
echo.
echo Your new JAR is ready at:
echo runelite-client\target\client-1.11.18-SNAPSHOT.jar
echo.
echo Close your EternalFarm clients and restart them!
echo.
pause

