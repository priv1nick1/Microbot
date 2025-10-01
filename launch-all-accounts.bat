@echo off
setlocal enabledelayedexpansion
title Microbot Multi-Account Launcher

echo ================================================
echo    Microbot Multi-Account Launcher
echo ================================================
echo.

:: Configuration
set "JAVA_PATH=C:\Program Files\Eclipse Adoptium\jdk-11.0.26.4-hotspot\bin\java.exe"
set "MICROBOT_JAR=C:\Users\MiniPC10\Desktop\Microbot\runelite-client\target\client-1.11.18-SNAPSHOT.jar"
set "ACCOUNTS_FILE=accounts.txt"
set "MEMORY=2048"
set "DELAY_SECONDS=30"

:: Check if files exist
if not exist "%JAVA_PATH%" (
    echo ERROR: Java not found at %JAVA_PATH%
    pause
    exit /b 1
)

if not exist "%MICROBOT_JAR%" (
    echo ERROR: Microbot JAR not found at %MICROBOT_JAR%
    pause
    exit /b 1
)

if not exist "%ACCOUNTS_FILE%" (
    echo ERROR: Accounts file not found: %ACCOUNTS_FILE%
    echo Please create accounts.txt with format: email:password
    pause
    exit /b 1
)

:: Count accounts
set ACCOUNT_NUM=0
for /f "tokens=*" %%a in (%ACCOUNTS_FILE%) do (
    set /a ACCOUNT_NUM+=1
)

echo Found %ACCOUNT_NUM% accounts in %ACCOUNTS_FILE%
echo.
echo Memory per client: %MEMORY%MB
echo Delay between launches: %DELAY_SECONDS% seconds
echo.
echo Starting launches...
echo.

:: Launch each account
set CURRENT=0
for /f "tokens=1,2 delims=:" %%a in (%ACCOUNTS_FILE%) do (
    set /a CURRENT+=1
    set "USERNAME=%%a"
    set "PASSWORD=%%b"
    
    echo [!CURRENT!/%ACCOUNT_NUM%] Launching: !USERNAME!
    
    start "Microbot - !USERNAME!" "%JAVA_PATH%" -Xmx%MEMORY%m -Dmicrobot.username=!USERNAME! -Dmicrobot.password=!PASSWORD! -Dmicrobot.members=true -jar "%MICROBOT_JAR%"
    
    if !CURRENT! LSS %ACCOUNT_NUM% (
        echo    Waiting %DELAY_SECONDS% seconds before next launch...
        timeout /t %DELAY_SECONDS% /nobreak >nul
        echo.
    )
)

echo.
echo ================================================
echo    All %ACCOUNT_NUM% clients launched!
echo ================================================
echo.
echo Press any key to close this window...
pause >nul

