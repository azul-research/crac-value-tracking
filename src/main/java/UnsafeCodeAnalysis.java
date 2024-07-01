import analysis.MethodAnalyser;
import input.ControlFlowGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static input.JarBytecodeExtractor.extractJarFile;

public class UnsafeCodeAnalysis {
    public static void main(String[] args) {

        String jarFilePath = "/Users/dariasuvorova/IdeaProjects/unsafecodeanalysis/main.jar";
        var classNames = extractJarFile(jarFilePath);

        System.out.println("Class names: " + classNames);

        ControlFlowGraph cfg = new ControlFlowGraph(jarFilePath);

        for (var className : classNames) {
            System.out.println("Name: " + getClassName(className));
            cfg.createClassCFG(getClassName(className));
        }

        MethodAnalyser analyser = new MethodAnalyser(cfg.getClassCFG("example.pack.Main").getMethodCFG("main"));



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
