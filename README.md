# CobolToJAVA Java Library

This project is a simple Java library for performing addition or subtraction based on user input. It is self-contained and does not require any native DLLs.

## Structure
- `src/main/java/com/example/dll/NativeLibrary.java`: Java class with methods to input and perform addition or subtraction.
- `build.bat`: Batch script to compile the Java code and package it as a JAR.

## How to Build
1. Open a command prompt in the project root.
2. Run `build.bat` to compile and package the library.

## How to Run
After building, run the Java class:
```
cd out
java com.example.dll.NativeLibrary
```

## Features
- Prompts the user to enter 'add' or 'sub' and two numbers.
- Performs the operation in Java and displays the result.

## Note
This version does not use JNI or require any DLLs. It is ready to be used as a JAR library or standalone Java application.
