package input;

import analysis.ClassFileAnalyser;
//import org.apache.bcel.classfile.ClassParser;
//import org.apache.bcel.classfile.JavaClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarBytecodeExtractor {

    String jarFilePath;
    JarFile jarFile;
    List<String> classNames = new ArrayList<>();




    public JarBytecodeExtractor(String jarFilePath) {
        this.jarFilePath = jarFilePath;
        try {
            this.jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            if (!entries.hasMoreElements()) {
                System.out.println("JAR IS EMPTY");
            }
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    classNames.add(entryName.replace(".class", "").replace("/", "."));
                }

            }


        } catch (java.io.IOException e) {
            throw new RuntimeException(e);

        }

        System.out.println(classNames);
    }

    public static void analyseJarFile(String jarFilePath) {
    }

    public static void getMethodsByteCode(String jarFilePath) {

        byte[] code = getMainMethodCode(jarFilePath);
        var analyser = new ClassFileAnalyser();
        assert code != null;
        analyser.analyseByteCodeOfMethod(code);

    }

    public List<String> getClassNames() {
        return classNames;
    }

    public static byte[] getMainMethodCode(String jarFilePath) {

        try {
            JarFile jarFile = new JarFile(jarFilePath);

            Enumeration<JarEntry> entries = jarFile.entries();
            if (!entries.asIterator().hasNext()) {
                System.out.println("JAR IS EMPTY");
            }

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();



            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
