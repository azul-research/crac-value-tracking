package analysis;

import entitites.Entity;
import input.ControlFlowGraph;
import javassist.*;
import javassist.bytecode.analysis.ControlFlow;
import output.CurrentState;

import java.util.*;

public class Analyser {


    static final String STANDARD_LIB_NAME = "java.lang.";

    static final String MAIN_FUNCTION_DESC = "([Ljava/lang/String;)V";
    static final String MAIN_FUNCTION_NAME = "main";


    static final Set<String> STANDARD_LIB_CLASSES = Set.of("java.lang.String", "java.lang.ClassLoader");

    ControlFlowGraph controlFlowGraph;
    String programJarPath;
    String mainClassName;

    public Analyser(String dirPath, String mainClassName) {
        this.programJarPath = dirPath;
        this.mainClassName = mainClassName;
        controlFlowGraph = new ControlFlowGraph(dirPath);
    }


    public CurrentState analyseMethod(String className, String methodName, List<Integer> derivativeArgs, String desc, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        prepareClass(className, loadedClasses, initialisedClasses);

        var methodCFG = controlFlowGraph.getMethodCFG(className, methodName, desc);
        var method = controlFlowGraph.getBehavior(className, methodName, desc);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs, this, new Entity(Entity.Type.UNDEFINED), loadedClasses, initialisedClasses);
        analyser.analyse();

        return analyser.getAnalysisResult();
    }


    private boolean classFromStandardLib(String className) {
        return STANDARD_LIB_CLASSES.contains(className);
    }

    public void prepareClass(String className, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        if (classFromStandardLib(className)) {
            return;
        }

        if (!loadedClasses.contains(className)) {
            loadClass(className, loadedClasses);
        }

        if (!initialisedClasses.containsKey(className)) {
            initialiseClass(className, loadedClasses, initialisedClasses);
        }
    }


    public CurrentState analyseProgram() {
        var loadedClasses = new HashSet<String>();
        var initialisedClasses = new HashMap<String, Map<String, Entity>>();

        return analyseMethod(mainClassName, MAIN_FUNCTION_NAME, List.of(0), MAIN_FUNCTION_DESC, loadedClasses, initialisedClasses);
    }


    private void loadClass(String className, Set<String> loadedClasses) {
        loadedClasses.add(className);

//        CtClass curClass = controlFlowGraph.getClass(className);
//        loadedClasses.add(getSuperClass(curClass).getName());
//
//        for ()
    }


    CtClass getSuperClass(CtClass ctClass) {
        try {
            return ctClass.getSuperclass();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private void initialiseClass(String className, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        initialisedClasses.put(className, new HashMap<>());

        ControlFlow.Block[] initializerCFG = controlFlowGraph.createInitializerCFG(className);
        var initializer = controlFlowGraph.getInitializer(className);
        CtClass curClass = controlFlowGraph.getClass(className);

        // default values to static fields
        CtField[] staticFields = getStaticFields(curClass);
        for (var field : staticFields) {
            initialisedClasses.get(className).put(field.getName(), new Entity(Entity.Type.NON_DERIVATIVE));
        }

        // initialise predecessors
        var declaringClass = getSuperClass(curClass);
        if (declaringClass != null) {
            prepareClass(declaringClass.getName(), loadedClasses, initialisedClasses);
        }

        // TODO
        try {
            for (var interf : curClass.getInterfaces()) {
                prepareClass(interf.getName(), loadedClasses, initialisedClasses);
            }

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }


        // execute initializer
        executeInitializer(initializer, initializerCFG, loadedClasses, initialisedClasses);

    }

    private CtField[] getStaticFields(CtClass cl) {
        var allFields = cl.getDeclaredFields();
        return Arrays.stream(allFields)
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .toArray(CtField[]::new);
    }


    private void executeInitializer(CtConstructor initializer, ControlFlow.Block[] initializerCFG, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        if (initializer == null) {
            return;
        }

        MethodAnalyser analyser = new MethodAnalyser(initializerCFG, initializer, List.of(), this, new Entity(Entity.Type.UNDEFINED), loadedClasses, initialisedClasses);
        analyser.analyse();
        CurrentState result = analyser.getAnalysisResult();

        loadedClasses.addAll(result.getLoadedClasses());
        initialisedClasses.putAll(result.getInitialisedClasses());
    }
}
