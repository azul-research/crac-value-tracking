package input;

import analysis.ClassFileAnalyser;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ByteSequence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarBytecodeExtractor {
    public static Set<Integer> analyseJarFile(String jarFilePath) {

        byte[] code = getMainMethodCode(jarFilePath);
        var analyser = new ClassFileAnalyser();
        assert code != null;
        analyser.analyseByteCodeOfMethod(code);

        return analyser.getUnsafeVars();

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
                        System.out.println(Arrays.toString(m.getArgumentTypes()));
//                        System.out.println("--------------------------------------");
//                        System.out.println(m.getCode());
//                        System.out.println("--------------------------------------");


                        if (m.getName().equals("main")) {
                            return m.getCode().getCode();

                        }

                    }

                    InputStream inputStream = jarFile.getInputStream(entry);
                    inputStream.close();
                    jarFile.close();

                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
