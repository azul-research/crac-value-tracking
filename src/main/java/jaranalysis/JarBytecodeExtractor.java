package jaranalysis;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarBytecodeExtractor {
    public static void main(String[] args) {

        String jarFilePath = "main.jar";
        String classFilePath = "src/main/java/org/example/Main.class";

        try {
            JarFile jarFile = new JarFile(jarFilePath);

            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".class")) {

                    System.out.println("Class: " + entryName);


                    ClassParser parser = new ClassParser(jarFilePath, entryName);
                    JavaClass javaClass = parser.parse();

                    System.out.println("Name: " + javaClass.getClassName());
                    System.out.println("Index: " + javaClass.getClassNameIndex());
                    System.out.println("Superclass: " + javaClass.getSuperclassName());

                    var methods = javaClass.getMethods();

                    System.out.println("Methods:");
                    for (var m : methods) {
                        System.out.println(m.getName());
//                        System.out.println(m.getCode());

                    }


//                JarEntry entry = jarFile.getJarEntry(classFilePath);

//                if (entry == null) {
//                    System.out.println("Class file " + classFilePath + " not found in JAR.");
//                    jarFile.close();
//                    return;
//                }

                InputStream inputStream = jarFile.getInputStream(entry);
                byte[] bytecode = inputStream.readAllBytes();
                inputStream.close();
                jarFile.close();

                System.out.println("Bytecode " + classFilePath + ":");
                for (byte b : bytecode) {
                    System.out.printf("%02x ", b);
                }
                System.out.println();
                }

}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
