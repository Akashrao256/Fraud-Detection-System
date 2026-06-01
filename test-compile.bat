@echo off
echo Testing compilation and running of the Fraud Detection System...
echo.
echo Step 1: Clean and compile the project
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)
echo Compilation successful!
echo.
echo Step 2: Attempting to run the Spring Boot application
echo Press Ctrl+C to stop the application when you're satisfied it's working
call mvn spring-boot:run
