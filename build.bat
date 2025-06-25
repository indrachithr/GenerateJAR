@echo off
REM Build and run the JavaCodeToJarService project from any location
setlocal

REM Get the directory of this script
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"

set SRC=src\main\java
set OUT=out
set LOG=build_output.log
if exist %LOG% del %LOG%
if not exist %OUT% mkdir %OUT%

REM Compile all Java source files and log output
javac -d %OUT% %SRC%\com\example\dll\util\*.java %SRC%\com\example\dll\*.java > %LOG% 2>&1
if errorlevel 1 (
    echo Compilation failed. Please check %LOG% for details.
    notepad %LOG%
    exit /b 1
)

REM Run the main Java service if argument is provided, log output
if not "%1"=="" (
    echo Running JavaCodeToJarService with %1 >> %LOG%
    java -cp %OUT%;. com.example.dll.JavaCodeToJarService %1 >> %LOG% 2>&1
    type %LOG%
    notepad %LOG%
    goto :eof
)

REM Run the main Java service with a prompt if no argument is provided, log output
set /p JAVA_FILE=Enter the path to a .java file to process (or leave blank to exit): 
if not "%JAVA_FILE%"=="" (
    echo Running JavaCodeToJarService with %JAVA_FILE% >> %LOG%
    java -cp %OUT%;. com.example.dll.JavaCodeToJarService "%JAVA_FILE%" >> %LOG% 2>&1
    type %LOG%
    notepad %LOG%
    goto :eof
)

echo No .java file provided. Exiting.
