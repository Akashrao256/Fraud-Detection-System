@echo off
REM Set JAVA_HOME to Java 24
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
echo Running Fraud Detection System with logs...
echo.
echo Building project...
call .\mvnw.cmd clean package -q
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)
echo Build successful!
echo.
echo Starting application on http://localhost:8080
echo Output saved to application.log
java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar > application.log 2>&1
echo Check application.log for details
pause
