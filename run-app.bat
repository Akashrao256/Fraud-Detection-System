@echo off
echo Running Fraud Detection System...
echo.
echo Step 1: Clean and compile the project
call mvn clean compile > compile-output.log
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed! Check compile-output.log for details.
    exit /b %ERRORLEVEL%
)
echo Compilation successful!
echo.
echo Step 2: Running the Spring Boot application
echo Output will be saved to spring-boot-output.log
echo Press Ctrl+C to stop the application when you're satisfied it's working
call mvn spring-boot:run > spring-boot-output.log
