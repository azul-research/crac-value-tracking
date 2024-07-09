package input;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class BytecodeExtractor {

    static public List<String> extractJarFile(String jarFilePath) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            List<String> classNames = new ArrayList<>();
            Enumeration<JarEntry> entries = jarFile.entries();
            if (!entries.hasMoreElements()) {
                System.out.println("JAR IS EMPTY");
            }
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getRealName();
                if (entryName.endsWith(".class")) {
                    classNames.add(entryName);
                }

            }

            return classNames;

        } catch (java.io.IOException e) {
            throw new RuntimeException(e);

        }

    }

    static public List<String> extractZipFile(String zipFilePath) {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            List<String> classNames = new ArrayList<>();
            var entries = zipFile.entries();
            if (!entries.hasMoreElements()) {
                System.out.println("JAR IS EMPTY");
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                System.out.println(entryName);

                if (entryName.endsWith(".class")) {
                    classNames.add(entryName);
                }

            }
            return classNames;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getSourceFile(String className) {

        Path p = Paths.get(URI.create("jrt:/")).resolve("/modules/java.base/" + className);
        return p.getFileName().toString();

    }

}
