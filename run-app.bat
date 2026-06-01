@echo off
REM Set JAVA_HOME to Java 24
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
echo Running Fraud Detection System...
echo.
echo Step 1: Building the project
call .\mvnw.cmd clean package -q
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)
echo Build successful!
echo.
echo Step 2: Running the application on http://localhost:8080
echo Press Ctrl+C to stop
java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar
pause
