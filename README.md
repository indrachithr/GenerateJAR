# Compile and generate JAR Java Library

This project provides a Java utility to compile and package arbitrary `.java` files into executable JARs and ZIPs, as well as simple add/subtract operations. It is self-contained and does not require any native DLLs.

## Structure
- `src/main/java/com/example/dll/JavaCodeToJarService.java`: Main service to compile and package any `.java` file into a JAR/ZIP.
- `src/main/java/com/example/dll/util/DynamicJarBuilder.java`: Utility for dynamic compilation and packaging.
- `src/main/java/com/example/dll/add/AddOperation.java` and `sub/SubOperation.java`: Example add/subtract operations.
- `build.bat`: Batch script to compile and run the project from any location, with user-specified output directory.

## How to Build & Run
1. Open a command prompt in the project root (or call `build.bat` from any directory).
2. When prompted, enter the output directory for compiled classes (or press Enter to use the default `out`).
3. To process a `.java` file:
   ```
   build.bat path\to\YourClass.java
   ```
   - The generated JAR and ZIP files will be placed in a subfolder named after the class inside your chosen output directory.
4. If no argument is given, you will be prompted to enter a `.java` file path interactively.

## Requirements
- Java JDK (not just JRE) must be installed and available in your PATH.
- Sufficient permissions to read/write files in the working/output directories.

## Features
- Accepts `.java` files from any location on your system.
- User can specify the output directory for compiled classes and generated JARs.
- Each JAR and ZIP is placed in a subfolder named after the class inside the output directory.
- Automatically renames files to match public class names if needed.
- Packages compiled classes into executable JAR and ZIP files.

## Note
This version does not use JNI or require any DLLs. It is ready to be used as a JAR library or standalone Java application.
