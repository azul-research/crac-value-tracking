import analysis.MethodAnalyser;
import input.ControlFlowGraph;


import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static input.JarBytecodeExtractor.extractJarFile;

public class UnsafeCodeAnalysis {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("""
                    Not enough arguments:\s
                    first argument - path to .jar file,\s
                    second argument - full name of Main class""");
            return;
        }

        String jarFilePath = args[0];
        var classNames = extractJarFile(jarFilePath);

        System.out.println("Class names: " + classNames);

        ControlFlowGraph cfg = new ControlFlowGraph(jarFilePath);

        for (var className : classNames) {
            System.out.println("Name: " + getClassName(className));
            cfg.createClassCFG(getClassName(className));
        }


        String className = args[1];
        String methodName = "main";
        var methodCFG = cfg.getClassCFG(className).getMethodCFG(methodName);
        var method = cfg.getMethod(className, methodName);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, Set.of(0));
        analyser.analyse();

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
