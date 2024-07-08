import analysis.MethodAnalyser;
import input.ControlFlowGraph;


import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static input.BytecodeExtractor.*;

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




//        System.out.println(ClassLoader.getSystemResource("java/lang/String.class"));


//        getSourceFile("java/lang/String.class");

//
//        ControlFlowGraph cfg1 = new ControlFlowGraph(getSourceFile("java"));
//


        analyseMethod(getSourceFile("java"), "java.lang.String", "charAt", List.of(0, 1));
//        cfg1.createClassCFG("java.lang.String");
//
//        String clName = "java.lang.String";
//        String mName = "length";
//        var methodCFG = cfg1.getClassCFG(clName).getMethodCFG(mName);
//        System.out.println(Arrays.toString(methodCFG));
//

        analyseMethod(args[0], args[1], "main", List.of(0));
//        String javaFilesPath = args[2];

//        String jarFilePath = args[0];
//        var classNames = extractJarFile(jarFilePath);
//
//        System.out.println("Class names: " + classNames);


//        String className = args[1];
//        String methodName = "main";
//        var methodCFG = cfg.getClassCFG(className).getMethodCFG(methodName);
//        var method = cfg.getMethod(className, methodName);

//        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, Set.of(0));
//        analyser.analyse();

    }


    public static MethodAnalyser analyseMethod(String dirPath, String className, String methodName, List<Integer> derivativeArgs) {

        ControlFlowGraph cfg = new ControlFlowGraph(dirPath);

        cfg.createClassCFG(className);

        var methodCFG = cfg.getClassCFG(className).getMethodCFG(methodName);
        var method = cfg.getMethod(className, methodName);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs);
        analyser.analyse();

        return analyser;
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
