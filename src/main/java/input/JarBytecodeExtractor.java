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

//        String jarFilePath = "/Users/dariasuvorova/IdeaProjects/unsafecodeanalysis/example2.jar";

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
                            byte[] code = m.getCode().getCode();
                            var analyser = new ClassFileAnalyser();
                            analyser.analyseByteCodeOfMethod(code);

                            return analyser.getUnsafeVars();

                        }

                    }

                    InputStream inputStream = jarFile.getInputStream(entry);
//                byte[] bytecode = inputStream.readAllBytes();
                    inputStream.close();
                    jarFile.close();
//
//                System.out.println("Bytecode :");
//           ¬     for (byte b : bytecode) {
//                    System.out.printf("%02x ", b);
//                }
//                System.out.println();
                }

            }





        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
