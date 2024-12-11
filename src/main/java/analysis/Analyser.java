/*
 * Copyright 2024 Azul Systems, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package analysis;

import entitites.Entity;
import entitites.derivatives.EnvironmentalRoot;
import entitites.derivatives.RootDerivative;
import entitites.derivatives.SystemPropertyRoot;
import input.ControlFlowGraph;
import javassist.*;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.analysis.ControlFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.*;

import java.util.*;

public class Analyser {
    private static final Logger logger = LogManager.getLogger(Analyser.class);

    static final String STANDARD_LIB_NAME = "java.lang.";

    static final String SYSTEM_CLASS_NAME = "java.lang.System";
    static final String MAIN_FUNCTION_DESC = "([Ljava/lang/String;)V";
    static final String MAIN_FUNCTION_NAME = "main";
    static final Set<String> GET_ENVIRONMENTAL_METHODS = Set.of("getenv");
    static final Set<String> GET_SYSTEM_PROPERTY_METHODS = Set.of("getProperties", "getProperty");


    static final Set<String> INITIALIZED_CLASSES = Set.of(
            "java.lang.String",
            "java.lang.ClassLoader",
            "java.lang.Object");

    ControlFlowGraph controlFlowGraph;
    String programJarPath;
    String mainClassName;

    public Analyser(String dirPath, String mainClassName) {
        this.programJarPath = dirPath;
        this.mainClassName = mainClassName;
        controlFlowGraph = new ControlFlowGraph(dirPath);
    }


    private boolean returnEnvironmentVariable(MyMethod method) {
        if (method.myClass().fullName().equals(SYSTEM_CLASS_NAME)) {
            return GET_ENVIRONMENTAL_METHODS.contains(method.methodName());
        }
        return false;
    }

    private boolean returnSystemProperty(MyMethod method) {
        if (method.myClass().fullName().equals(SYSTEM_CLASS_NAME)) {
            return GET_SYSTEM_PROPERTY_METHODS.contains(method.methodName());
        }
        return false;
    }

    public MethodAnalyserBase analyseMethod(String className, String methodName, Map<Integer, Entity> derivativeArgs, String desc, Optional<Entity> currentObject, JVMState jvmState, ArrayDeque<MyMethod> stacktrace) {

        var myClass = prepareClass(className, jvmState, stacktrace);

        var methodCFG = controlFlowGraph.getMethodCFG(className, methodName, desc);
        var method = controlFlowGraph.getBehavior(className, methodName, desc);

        var myMethod = new MyMethod(myClass, methodName, desc);

        CurrentState result = null;

        if (stacktrace.contains(myMethod) || methodCFG.length == 0) {
            result = methodEmptyState(method, jvmState);
        }

        if (Modifier.isNative(method.getModifiers())) {
            result = methodEmptyState(method, jvmState);
        }

        if (returnEnvironmentVariable(myMethod)) {
            result = methodWithEnvironmentalReturn(method, jvmState);
        }
        if (returnSystemProperty(myMethod)) {
            result = methodWithSysPropertyReturn(method, jvmState);
        }


        if (result != null) {
            return new MethodAnalyserBase(result);
        }

        return analyseMethodInternal(stacktrace, myMethod, method, methodCFG, derivativeArgs, currentObject, jvmState);
    }


    private MethodAnalyser analyseMethodInternal(ArrayDeque<MyMethod> stacktrace, MyMethod myMethod, CtBehavior method, ControlFlow.Block[] methodCFG, Map<Integer, Entity> derivativeArgs, Optional<Entity> currentObject, JVMState jvmState) {
        stacktrace.push(myMethod);

        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, derivativeArgs, this, currentObject, jvmState, stacktrace);
        analyser.analyseAllBlocks();

        stacktrace.pop();

        return analyser;
    }

    CurrentState methodEmptyState(CtBehavior method, JVMState jvmState) {
        var result = CurrentState.getEmptyState(0, Map.of(), jvmState);
        if (methodHasReturnValue(method)) {
            result.updateReturnState(new Entity(Entity.Type.UNDEFINED));
        }
        return result;
    }

    CurrentState methodWithEnvironmentalReturn(CtBehavior method, JVMState jvmState) {
        var result = CurrentState.getEmptyState(0, Map.of(), jvmState);
        if (methodHasReturnValue(method)) {
            result.updateReturnState(new Entity(Entity.Type.DERIVATIVE_SET, new EnvironmentalRoot()));
        }
        return result;
    }

    CurrentState methodWithSysPropertyReturn(CtBehavior method, JVMState jvmState) {
        var result = CurrentState.getEmptyState(0, Map.of(), jvmState);
        if (methodHasReturnValue(method)) {
            result.updateReturnState(new Entity(Entity.Type.DERIVATIVE_SET, new SystemPropertyRoot()));
        }
        return result;
    }

    static boolean methodHasReturnValue(CtBehavior behavior) {
        return !behavior.getMethodInfo().getDescriptor().endsWith("V");
    }

    private boolean classFromStandardLib(String className) {
        return INITIALIZED_CLASSES.contains(className);
    }

    public MyClass prepareClass(String className, JVMState jvmState, ArrayDeque<MyMethod> stacktrace) {

        MyClass myClass = new MyClass(className);

        if (!jvmState.containsLoadedClass(myClass)) {
            jvmState.addLoadedClass(myClass);
        }

        if (!jvmState.containsInitializedClass(myClass)) {
            initialiseClass(myClass, jvmState, stacktrace);
        }

        return myClass;
    }

    private String getMainArgumentName() {
        try {

            LocalVariableAttribute attr = (LocalVariableAttribute) controlFlowGraph.getClass(mainClassName).getMethod(MAIN_FUNCTION_NAME, MAIN_FUNCTION_DESC).getMethodInfo().getCodeAttribute().getAttribute(LocalVariableAttribute.tag);
            return attr.variableNameByIndex(0);

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public CurrentState analyseProgram() {
        var jvmState = new JVMState();
        ArrayDeque<MyMethod> stacktrace = new ArrayDeque<>();

//        for (var cl : INITIALIZED_CLASSES) {
//            prepareClass(cl, jvmState, stacktrace);
//        }

        var rootDerivative = new RootDerivative(getMainArgumentName());

        return analyseMethod(mainClassName, MAIN_FUNCTION_NAME, Map.of(0, new Entity(Entity.Type.DERIVATIVE_SET, rootDerivative)), MAIN_FUNCTION_DESC, Optional.empty(), jvmState, stacktrace).getAnalysisResult();
    }


    CtClass getSuperClass(CtClass ctClass) {
        try {
            return ctClass.getSuperclass();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private void initialiseClass(MyClass myClass, JVMState jvmState, ArrayDeque<MyMethod> stacktrace) {
        jvmState.addInitialisedClass(myClass, new ClassStaticFields(myClass));

        ControlFlow.Block[] initializerCFG = controlFlowGraph.createInitializerCFG(myClass.fullName());
        var initializer = controlFlowGraph.getInitializer(myClass.fullName());
        CtClass curClass = controlFlowGraph.getClass(myClass.fullName());

        // default values to static fields
        CtField[] staticFields = getStaticFields(curClass);
        for (var field : staticFields) {
            jvmState.addClassStaticField(myClass, field.getName(), new Entity(Entity.Type.NON_DERIVATIVE));
        }

        // initialise predecessors
        var declaringClass = getSuperClass(curClass);
        if (declaringClass != null) {
            prepareClass(declaringClass.getName(), jvmState, stacktrace);
        }

        // TODO
        try {
            for (var interf : curClass.getInterfaces()) {
                prepareClass(interf.getName(), jvmState, stacktrace);
            }

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        // execute initializer
        executeInitializer(initializer, initializerCFG, jvmState, stacktrace, myClass);

    }

    private CtField[] getStaticFields(CtClass cl) {
        var allFields = cl.getDeclaredFields();
        return Arrays.stream(allFields)
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .toArray(CtField[]::new);
    }


    private void executeInitializer(CtConstructor initializer, ControlFlow.Block[] initializerCFG, JVMState jvmState, ArrayDeque<MyMethod> stacktrace, MyClass myClass) {
        if (initializer == null) {
            return;
        }

        MyMethod myMethod = new MyMethod(myClass, "<clinit>", "()V");
        var result = analyseMethodInternal(stacktrace, myMethod, initializer, initializerCFG, Map.of(), Optional.empty(), jvmState).getAnalysisResult();

        jvmState.addLoadedClasses(result.getLoadedClasses());
        jvmState.addInitialisedClasses(result.getInitialisedClasses());
    }
}
