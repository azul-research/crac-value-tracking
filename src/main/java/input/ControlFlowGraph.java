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
