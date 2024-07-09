package analysis;

import input.ControlFlowGraph;

import java.util.List;

public class Analyser {


    ControlFlowGraph controlFlowGraph;

    String programJarPath;

    String mainClassName;

    public Analyser(String dirPath, String mainClassName) {
        this.programJarPath = dirPath;
        this.mainClassName = mainClassName;
        controlFlowGraph = new ControlFlowGraph(dirPath);

    }

    private MethodAnalyser analyseMethod(String className, String methodName, List<Integer> derivativeArgs) {

        controlFlowGraph.createClassCFG(className);

        var methodCFG = controlFlowGraph.getClassCFG(className).getMethodCFG(methodName);
        var method = controlFlowGraph.getMethod(className, methodName);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs, this);
        analyser.analyse();

        return analyser;
    }


    public void analyseProgram() {
        analyseMethod(mainClassName, "main", List.of(0));

    }
}
