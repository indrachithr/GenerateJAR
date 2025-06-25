package com.example.dll.util;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.*;
import java.util.jar.*;
import java.io.*;

public class DynamicJarBuilder {
       /**
     * Accepts a .java file path, reads the file, formats the code, and compiles it to a JAR.
     * @param javaFilePath The path to the .java file
     * @return The name of the generated JAR file, or an error message
     */
    public static String buildJarFromJavaFile(String javaFilePath) {
        System.out.println("[LOG] buildJarFromJavaFile called with path: " + javaFilePath);
        try {
            java.io.File file = new java.io.File(javaFilePath);
            if (!file.exists() || !file.isFile() || !javaFilePath.endsWith(".java")) {
                return "Error: Provided path is not a valid .java file.";
            }
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            System.out.println("[LOG1] buildJarFromJavaFile called with path: " + javaFilePath);
            if (compiler == null) throw new IllegalStateException("No Java compiler available.");
            // Always use absolute path for parentDir
            String parentDir = file.getParent();
            if (parentDir == null) parentDir = new java.io.File(".").getAbsolutePath();
            else parentDir = new java.io.File(parentDir).getAbsolutePath();
            System.out.println("[LOG2] Using parentDir: " + parentDir);
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            int result = compiler.run(null, null, errorStream, "-d", parentDir, file.getAbsolutePath());
            if (result == 0) {
                // Compilation succeeded, proceed as normal
                String fileName = file.getName();
                String className = fileName.substring(0, fileName.length() - 5); // remove .java
                String classFileName = className + ".class";
                String classFilePath = parentDir + File.separator + classFileName;
                String jarFileName = className + ".jar";
                System.out.println("[LOG] Compilation successful: " + classFileName);
                // Create manifest with Main-Class
                Manifest manifest = new Manifest();
                manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, className);
                // Create JAR file with manifest, including only .class file
                try (JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFileName), manifest)) {
                    // Add .class file at root
                    JarEntry classEntry = new JarEntry(classFileName);
                    classEntry.setTime(System.currentTimeMillis());
                    target.putNextEntry(classEntry);
                    byte[] classBytes = Files.readAllBytes(Paths.get(classFilePath));
                    target.write(classBytes, 0, classBytes.length);
                    target.closeEntry();
                }
                System.out.println("[LOG] JAR created: " + jarFileName);
                // Clean up .class file after packaging
                Files.deleteIfExists(Paths.get(classFilePath));
                // Add .jar file to a zip archive
                String zipFileName = className + ".zip";
                try (java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(zipFileName))) {
                    // Add .jar file
                    java.util.zip.ZipEntry jarEntry = new java.util.zip.ZipEntry(jarFileName);
                    zipOut.putNextEntry(jarEntry);
                    byte[] jarBytes = Files.readAllBytes(Paths.get(jarFileName));
                    zipOut.write(jarBytes, 0, jarBytes.length);
                    zipOut.closeEntry();
                    // Add .java file for reference
                    java.util.zip.ZipEntry srcEntry = new java.util.zip.ZipEntry(fileName);
                    zipOut.putNextEntry(srcEntry);
                    zipOut.write(Files.readAllBytes(file.toPath()));
                    zipOut.closeEntry();
                }
                System.out.println("[LOG] ZIP created: " + zipFileName);
                return jarFileName;
            } else {
                // Compilation failed, try to fix filename and format code
                String originalCode = new String(Files.readAllBytes(file.toPath()));
                String cleanedCode = originalCode.replace("\\", "");
                // Extract public class name
                String className = null;
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("public\\s+class\\s+([A-Za-z_][A-Za-z0-9_]*)").matcher(cleanedCode);
                if (matcher.find()) {
                    className = matcher.group(1);
                } else {
                    className = file.getName().substring(0, file.getName().length() - 5);
                }
                String expectedFileName = className + ".java";
                Path expectedFilePath = parentDir == null ? Paths.get(expectedFileName) : Paths.get(parentDir, expectedFileName);
                if (!file.getName().equals(expectedFileName)) {
                    Files.write(expectedFilePath, cleanedCode.getBytes());
                    Files.delete(file.toPath());
                    file = expectedFilePath.toFile();
                    javaFilePath = expectedFilePath.toString();
                } else {
                    Files.write(file.toPath(), cleanedCode.getBytes());
                }
                // Try to compile again after renaming/formatting
                errorStream = new ByteArrayOutputStream();
                result = compiler.run(null, null, errorStream, "-d", parentDir, file.getAbsolutePath());
                if (result != 0) {
                    String errors = errorStream.toString();
                    System.out.println("--- Syntax validation failed after formatting ---");
                    System.out.println(errors);
                    return "Error: Java syntax validation failed after formatting.\n" + errors;
                }
                // If valid, proceed to package as JAR (same as above)
                String classFileName = className + ".class";
                String classFilePath = parentDir + File.separator + classFileName;
                String jarFileName = className + ".jar";
                System.out.println("[LOG] Compilation successful after formatting: " + classFileName);
                // Create manifest with Main-Class
                Manifest manifest = new Manifest();
                manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, className);
                // Create JAR file with manifest, including only .class file
                try (JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFileName), manifest)) {
                    // Add .class file at root
                    JarEntry classEntry = new JarEntry(classFileName);
                    classEntry.setTime(System.currentTimeMillis());
                    target.putNextEntry(classEntry);
                    byte[] classBytes = Files.readAllBytes(Paths.get(classFilePath));
                    target.write(classBytes, 0, classBytes.length);
                    target.closeEntry();
                }
                System.out.println("[LOG] JAR created: " + jarFileName);
                Files.deleteIfExists(Paths.get(classFilePath));
                String zipFileName = className + ".zip";
                try (java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(zipFileName))) {
                    java.util.zip.ZipEntry jarEntry = new java.util.zip.ZipEntry(jarFileName);
                    zipOut.putNextEntry(jarEntry);
                    byte[] jarBytes = Files.readAllBytes(Paths.get(jarFileName));
                    zipOut.write(jarBytes, 0, jarBytes.length);
                    zipOut.closeEntry();
                    java.util.zip.ZipEntry srcEntry = new java.util.zip.ZipEntry(expectedFileName);
                    zipOut.putNextEntry(srcEntry);
                    zipOut.write(Files.readAllBytes(expectedFilePath));
                    zipOut.closeEntry();
                }
                System.out.println("[LOG] ZIP created: " + zipFileName);
                return jarFileName;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unexpected exception - " + e.getMessage();
        }
    }
}
