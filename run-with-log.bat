@echo off
echo Running Fraud Detection System...
echo.
echo Output will be saved to application.log
call mvn spring-boot:run > application.log 2>&1
echo.
echo Check application.log for details
