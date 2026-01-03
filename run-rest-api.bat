@echo off
echo Compiling project...
call mvn compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Starting REST API Server on port 8081...
    echo API Base URL: http://localhost:8081/api
    echo.
    call mvn exec:java -Dexec.mainClass=api.RestApiServer
) else (
    echo.
    echo Compilation failed! Please fix errors and try again.
    exit /b 1
)

