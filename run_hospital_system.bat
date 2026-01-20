@echo off
echo HU Hospital Management System Launcher
echo =====================================
echo.

echo Testing compilation...
javac -cp "src" src/hu_hospital/management/system/CompilationTest.java
if %errorlevel% neq 0 (
    echo Compilation failed. Please check your Java installation.
    pause
    exit /b 1
)

echo Running compilation test...
java -cp "src" hu_hospital.management.system.CompilationTest
if %errorlevel% neq 0 (
    echo Core system test failed.
    pause
    exit /b 1
)

echo.
echo Launching Hospital Management System...
echo.

REM Try to run with JavaFX
java -cp "src" hu_hospital.management.system.StandaloneHospitalApp

if %errorlevel% neq 0 (
    echo.
    echo JavaFX version failed. Trying console version...
    java -cp "src" hu_hospital.management.system.TestApplication
)

pause