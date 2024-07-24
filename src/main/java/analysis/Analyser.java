package analysis;

import entitites.Entity;
import input.ControlFlowGraph;
import javassist.*;
import javassist.bytecode.analysis.ControlFlow;
import output.ClassStaticFields;
import output.CurrentState;
import output.JVMState;
import output.MyClass;

import java.util.*;

public class Analyser {


    static final String STANDARD_LIB_NAME = "java.lang.";

    static final String MAIN_FUNCTION_DESC = "([Ljava/lang/String;)V";
    static final String MAIN_FUNCTION_NAME = "main";


    static final Set<String> STANDARD_LIB_CLASSES = Set.of("java.lang.String", "java.lang.ClassLoader", "java.lang.Object");

    ControlFlowGraph controlFlowGraph;
    String programJarPath;
    String mainClassName;

    public Analyser(String dirPath, String mainClassName) {
        this.programJarPath = dirPath;
        this.mainClassName = mainClassName;
        controlFlowGraph = new ControlFlowGraph(dirPath);
    }


    public CurrentState analyseMethod(String className, String methodName, List<Integer> derivativeArgs, String desc, Optional<Entity> currentObject, JVMState jvmState) {
        prepareClass(className, jvmState);

        var methodCFG = controlFlowGraph.getMethodCFG(className, methodName, desc);
        var method = controlFlowGraph.getBehavior(className, methodName, desc);

        if (Modifier.isNative(method.getModifiers())) {
            var nativeMethodState = CurrentState.getEmptyState(0, Map.of(), jvmState);
            if (!method.getMethodInfo().getDescriptor().endsWith("V")) {
                nativeMethodState.updateReturnState(new Entity(Entity.Type.NON_DERIVATIVE));
            }
            return nativeMethodState;
        }
        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs, this, currentObject, jvmState);
        analyser.analyse();

        return analyser.getAnalysisResult();
    }


    private boolean classFromStandardLib(String className) {
        return STANDARD_LIB_CLASSES.contains(className);
    }

    public void prepareClass(String className, JVMState jvmState) {
//        if (classFromStandardLib(className)) {
//            return;
//        }

        MyClass myClass = new MyClass(className);
        if (!jvmState.containsLoadedClass(myClass)) {
            jvmState.addLoadedClass(myClass);
        }

        if (!jvmState.containsInitializedClass(myClass)) {
            initialiseClass(myClass, jvmState);
        }
    }


    public CurrentState analyseProgram() {
        var jvmState = new JVMState();
        return analyseMethod(mainClassName, MAIN_FUNCTION_NAME, List.of(0), MAIN_FUNCTION_DESC, Optional.empty(), jvmState);
    }


    CtClass getSuperClass(CtClass ctClass) {
        try {
            return ctClass.getSuperclass();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private void initialiseClass(MyClass myClass, JVMState jvmState) {
        jvmState.addInitialisedClass(myClass, new ClassStaticFields(myClass));

        ControlFlow.Block[] initializerCFG = controlFlowGraph.createInitializerCFG(myClass.getFullName());
        var initializer = controlFlowGraph.getInitializer(myClass.getFullName());
        CtClass curClass = controlFlowGraph.getClass(myClass.getFullName());

        // default values to static fields
        CtField[] staticFields = getStaticFields(curClass);
        for (var field : staticFields) {
            jvmState.addClassStaticField(myClass, field.getName(), new Entity(Entity.Type.NON_DERIVATIVE));
        }

        // initialise predecessors
        var declaringClass = getSuperClass(curClass);
        if (declaringClass != null) {
            prepareClass(declaringClass.getName(), jvmState);
        }

        // TODO
        try {
            for (var interf : curClass.getInterfaces()) {
                prepareClass(interf.getName(), jvmState);
            }

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        // execute initializer
        executeInitializer(initializer, initializerCFG, jvmState);

    }

    private CtField[] getStaticFields(CtClass cl) {
        var allFields = cl.getDeclaredFields();
        return Arrays.stream(allFields)
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .toArray(CtField[]::new);
    }


    private void executeInitializer(CtConstructor initializer, ControlFlow.Block[] initializerCFG, JVMState jvmState) {
        if (initializer == null) {
            return;
        }

        MethodAnalyser analyser = new MethodAnalyser(initializerCFG, initializer, List.of(), this, Optional.empty(), jvmState);
        analyser.analyse();
        CurrentState result = analyser.getAnalysisResult();

        jvmState.addLoadedClasses(result.getLoadedClasses());
        jvmState.addInitialisedClasses(result.getInitialisedClasses());
    }
}
