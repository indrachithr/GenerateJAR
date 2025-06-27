@echo off
REM Build and run the JavaCodeToJarService project from any location
setlocal

REM Get the directory of this script
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"

set SRC=src\main\java

REM Prompt user for OUT directory or use default
set OUT=
set /p OUT=Enter the output directory for compiled classes (default: out): 
if "%OUT%"=="" set OUT=out
if not exist %OUT% mkdir %OUT%

REM Compile all Java source files
javac -d %OUT% %SRC%\com\example\dll\util\*.java %SRC%\com\example\dll\*.java
if errorlevel 1 (
    echo Compilation failed. Please check the error messages above.
    exit /b 1
)

REM Run the main Java service if argument is provided
if not "%1"=="" (
    java -cp "%OUT%;." com.example.dll.JavaCodeToJarService %1
    goto :eof
)

REM Run the main Java service with a prompt if no argument is provided
set /p JAVA_FILE=Enter the path to a .java file to process (or leave blank to exit): 
if not "%JAVA_FILE%"=="" (
    java -cp "%OUT%;." com.example.dll.JavaCodeToJarService "%JAVA_FILE%"
    goto :eof
)

echo No .java file provided. Exiting.
