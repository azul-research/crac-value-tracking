package input;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.*;

import java.util.*;

public class ControlFlowGraph {

    private ClassPool pool;
    private final Map<String, ClassCFG> classesCFG = new HashMap<>();

    public ClassPool getPool() {
        return pool;
    }

    public ControlFlow.Block[] getMethodCFG(String className, String methodName, String methodDesc) {
        return classesCFG.get(className).getMethodCFG(methodName, methodDesc);
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

    public ControlFlow.Block[] createMethodCFG(String className, String methodName, String desc) {
        try {
            CtClass ctClass = pool.get(className);
            CtMethod method = ctClass.getMethod(methodName, desc);

            if (!classesCFG.containsKey(className)) {
                classesCFG.put(className, new ClassCFG());
            }
            ClassCFG classCFG = classesCFG.get(className);


            classCFG.addMethodCFG(createMethodCFG(method), methodName, desc);

            return classCFG.getMethodCFG(methodName, desc);


        } catch (NotFoundException | BadBytecode e) {
            throw new RuntimeException(e);
        }

    }

    public CtMethod getMethod(String className, String methodName, String desc) {
        try {
            CtClass ctClass = pool.get(className);
            return ctClass.getMethod(methodName, desc);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    public ControlFlow.Block[] createInitializerCFG(String className) {
        try {

            CtClass ctClass = pool.get(className);
            CtConstructor ctConstructor = ctClass.getClassInitializer();

            if (!classesCFG.containsKey(className)) {
                classesCFG.put(className, new ClassCFG());
            }


            ClassCFG classCFG = classesCFG.get(className);


            classCFG.addMethodCFG(createMethodCFG(ctConstructor), "<clinit>", "()V");

            return classCFG.getMethodCFG("<clinit>", "()V");


        } catch (NotFoundException | BadBytecode e) {
            throw new RuntimeException(e);
        }

    }


    private ControlFlow.Block[] createMethodCFG(CtBehavior method) throws BadBytecode {

        MethodInfo methodInfo = method.getMethodInfo();
        ControlFlow controlFlow = new ControlFlow(method.getDeclaringClass(), methodInfo);
        return controlFlow.basicBlocks();
    }

}
