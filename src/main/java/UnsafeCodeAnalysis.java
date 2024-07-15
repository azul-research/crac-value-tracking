import analysis.Analyser;


import java.io.IOException;

import java.util.Arrays;
import java.util.Objects;


public class UnsafeCodeAnalysis {
    public static void main(String[] args) throws IOException {

        if (args.length < 3) {
            System.out.println("""
                    Not enough arguments:\s
                    first argument - path to .jar file,\s
                    second argument - full name of Main class,\s
                    third argument - path to Java""");
            return;
        }

        Analyser analyser = new Analyser(args[0], args[1]);


        analyser.analyseProgram();

//        analyseMethod(getSourceFile("java"), "java.lang.String", "contentEquals", List.of(0, 1));
//        analyseMethod(args[0], args[1], "main", List.of(0));

    }





    public static String getClassName(String classPath) {
        String[] splittedPath = classPath.split("/");
        if (splittedPath.length < 2) {
            System.out.println("INCORRECT CLASS PATH");
            return null;
        }
        int i = 0;

        while (i < splittedPath.length && !Objects.equals(splittedPath[i], "java")) {
            i++;
        }
        if (i == splittedPath.length) {
            System.out.println("INCORRECT CLASS PATH");
            return null;
        }

        splittedPath = Arrays.copyOfRange(splittedPath, i + 1, splittedPath.length);

        String result = String.join(".", splittedPath);
        return result.replace(".class", "");


    }

}
