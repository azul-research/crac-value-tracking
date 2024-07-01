package input;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarBytecodeExtractor {

    static public List<String> extractJarFile(String jarFilePath) {
        try {
            List<String> classNames = new ArrayList<>();

            JarFile jarFile = new JarFile(jarFilePath);
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

}
