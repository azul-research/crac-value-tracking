package analysis;

import entitites.Entity;
import input.ControlFlowGraph;
import javassist.NotFoundException;
import javassist.bytecode.analysis.ControlFlow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public MethodAnalyser analyseMethod(String className, String methodName, List<Integer> derivativeArgs, String desc, Set<String> loadedClasses, Set<String> initialisedClasses) {

        var methodCFG = controlFlowGraph.createMethodCFG(className, methodName, desc);
        var method = controlFlowGraph.getMethod(className, methodName, desc);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs, this, new Entity(Entity.Type.UNDEFINED), loadedClasses, initialisedClasses);
        analyser.analyse();

        return analyser;
    }


    public MethodAnalyser analyseProgram() {
        var loadedClasses = new HashSet<String>();
        var initialisedClasses = new HashSet<String>();

        prepareClass(mainClassName, loadedClasses, initialisedClasses);
        return analyseMethod(mainClassName, "main", List.of(0), "([Ljava/lang/String;)V", loadedClasses, initialisedClasses);
    }


    private void prepareClass(String className, Set<String> loadedClasses, Set<String> initialisedClasses) {
        if (!loadedClasses.contains(className)) {
            loadClass(className, loadedClasses);
        }
        if (!initialisedClasses.contains(className)) {
            initialiseClass(className, initialisedClasses);
        }
    }


    private void loadClass(String className, Set<String> loadedClasses) {
        loadedClasses.add(className);
    }

    private void initialiseClass(String className, Set<String> initialisedClasses) {
        initialisedClasses.add(className);
        ControlFlow.Block[] cfg = controlFlowGraph.createInitializerCFG(className);


        System.out.println(Arrays.toString(cfg));

        MethodAnalyser analyser = new MethodAnalyser(cfg, method, List.of(), this, new Entity(Entity.Type.UNDEFINED), loadedClasses, initialisedClasses);
        analyser.analyse();
    }
}
