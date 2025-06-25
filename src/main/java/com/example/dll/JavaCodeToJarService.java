package com.example.dll;

import com.example.dll.util.DynamicJarBuilder;
import java.io.InputStream;

public class JavaCodeToJarService {
   
      /**
     * Accepts a .java file as an InputStream, writes it to disk, compiles it, and creates an executable JAR file.
     * @param inputStream The InputStream containing the .java file content
     * @param fileName The name to use for the .java file (should end with .java)
     * @return The name of the generated JAR file, or an error message
     */
    public static String processJavaFileInputStream(InputStream inputStream, String fileName) {
        if (fileName == null || !fileName.endsWith(".java")) {
            return "Error: fileName must end with .java";
        }
        java.io.File javaFile = new java.io.File(fileName);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(javaFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            return "Error writing .java file: " + e.getMessage();
        }
        if (!javaFile.exists() || javaFile.length() == 0) {
            return "Error: .java file is empty or could not be written.";
        }
        // Compile and package using DynamicJarBuilder
        System.out.println("[LOG] Compiling and packaging: " + fileName);
        String result = DynamicJarBuilder.buildJarFromJavaFile(fileName);
        // Optionally clean up the .java file after packaging
        // javaFile.delete();
        return result;
    }

    // Accept raw Java code as a string, a .java file path, or from standard input
    public static void main(String[] args) throws Exception {
        System.out.println("[LOG] JavaCodeToJarService.main called");
        String result = null;
        if (args != null && args.length > 0) {
            String firstArg = args[0];
            java.io.File file = new java.io.File(firstArg);
            System.out.println("[LOG] Argument received: " + firstArg);
            if (firstArg.endsWith(".java") && file.exists() && file.isFile()) {
                if (file.length() == 0) {
                    System.out.println("Warning: The provided .java file is empty. Please provide a file with Java code.");
                    return;
                }
                System.out.println("[LOG] Valid .java file detected. Calling buildJarFromJavaFile.");
                result = DynamicJarBuilder.buildJarFromJavaFile(firstArg);
                System.out.println("[LOG] Result: " + result);
                System.out.println(result);
                return;
            } else {
                System.out.println("Error: Please provide a valid .java file path as the first argument.\nUsage: java -cp out com.example.dll.JavaCodeToJarService MyClass.java");
                return;
            }
        } else {
            System.out.println("Error: Please provide a .java file path as the first argument.\nUsage: java -cp out com.example.dll.JavaCodeToJarService MyClass.java");
            return;
        }
    }
}
