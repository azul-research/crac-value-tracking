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


package input;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.*;

import java.util.*;

public class ControlFlowGraph {

    private static final String CONSTRUCTOR_NAME = "<init>";

    private ClassPool pool;
    private final Map<String, ClassCFG> classesCFG = new HashMap<>();

    public ClassPool getPool() {
        return pool;
    }


    public CtClass getClass(String className) {
        try {
            return pool.get(className);

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean containsMethod(String className, String methodName, String methodDesc) {
        return classesCFG.containsKey(className) && classesCFG.get(className).containsMethod(methodName, methodDesc);

    }

    public ControlFlow.Block[] getMethodCFG(String className, String methodName, String methodDesc) {
        if (containsMethod(className, methodName, methodDesc)) {
            return classesCFG.get(className).getMethodCFG(methodName, methodDesc);
        }

        return createMethodCFG(className, methodName, methodDesc);
    }

    public ControlFlowGraph(String filePath) {
        addClassPath(filePath);
    }


    public void addClassPath(String filePath) {
        try {
            pool = ClassPool.getDefault();
            pool.appendClassPath(filePath);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ControlFlow.Block[] createMethodCFG(String className, String methodName, String desc) {
        try {
            CtBehavior behavior = getBehavior(className, methodName, desc);
            if (!classesCFG.containsKey(className)) {
                classesCFG.put(className, new ClassCFG());
            }
            ClassCFG classCFG = classesCFG.get(className);

            classCFG.addMethodCFG(createMethodCFG(behavior), methodName, desc);

            return classCFG.getMethodCFG(methodName, desc);

        } catch (BadBytecode e) {
            throw new RuntimeException(e);
        }

    }

    public CtBehavior getBehavior(String className, String behaviorName, String desc) {
        try {
            CtClass ctClass = pool.get(className);

            if (behaviorName.equals(CONSTRUCTOR_NAME)) {
                return ctClass.getConstructor(desc);
            }

            return ctClass.getMethod(behaviorName, desc);

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//
//    public CtBehavior getMethod(String className, String methodName, String desc) {
//        return getBehaivor(className, methodName, desc);
//    }

    public ControlFlow.Block[] createInitializerCFG(String className) {
        try {

            CtClass ctClass = pool.get(className);
            CtConstructor ctConstructor = ctClass.getClassInitializer();
            if (ctConstructor == null) {
                return new ControlFlow.Block[0];
            }

            if (!classesCFG.containsKey(className)) {
                classesCFG.put(className, new ClassCFG());
            }

            return createMethodCFG(ctConstructor);


        } catch (NotFoundException | BadBytecode e) {
            throw new RuntimeException(e);
        }

    }


    public CtConstructor getInitializer(String className) {
        try {
            return pool.get(className).getClassInitializer();
        } catch (NotFoundException e) {
            return null;
        }
    }


    private ControlFlow.Block[] createMethodCFG(CtBehavior method) throws BadBytecode {

        MethodInfo methodInfo = method.getMethodInfo();
        ControlFlow controlFlow = new ControlFlow(method.getDeclaringClass(), methodInfo);
        return controlFlow.basicBlocks();
    }

}
