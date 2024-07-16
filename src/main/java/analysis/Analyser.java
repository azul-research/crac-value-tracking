package analysis;

import entitites.Entity;
import input.ControlFlowGraph;

import java.util.List;

import static input.BytecodeExtractor.getSourceFile;

public class Analyser {

    ControlFlowGraph controlFlowGraph;
    String programJarPath;
    String mainClassName;

    public Analyser(String dirPath, String mainClassName) {
        this.programJarPath = dirPath;
        this.mainClassName = mainClassName;
        controlFlowGraph = new ControlFlowGraph(dirPath);
//        controlFlowGraph.addClassPath(getSourceFile("java"));

    }

    public MethodAnalyser analyseMethod(String className, String methodName, List<Integer> derivativeArgs, String desc) {

        controlFlowGraph.createClassCFG(className);

        var methodCFG = controlFlowGraph.getClassCFG(className).getMethodCFG(methodName, desc);
        var method = controlFlowGraph.getMethod(className, methodName, desc);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs, this, new Entity(Entity.Type.UNDEFINED));
        analyser.analyse();

        return analyser;
    }


    public MethodAnalyser analyseProgram() {
        return analyseMethod(mainClassName, "main", List.of(0), "([Ljava/lang/String;)V");

    }
}
