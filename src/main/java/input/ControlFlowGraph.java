package input;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.*;

import java.util.*;

public class ControlFlowGraph {

    private ClassPool pool;
    private final Map<String, ClassCFG> classesCFG = new HashMap<>();


    public Map<String, ClassCFG> getCFG() {
        return classesCFG;
    }

    public ClassCFG getClassCFG(String name) {
        return classesCFG.get(name);
    }

    public ControlFlowGraph(String jarFilePath) {
        addClassPath(jarFilePath);
    }


    public void addClassPath(String filePath) {
        try {
            pool = ClassPool.getDefault();
            pool.appendClassPath(filePath);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void createClassCFG(String className) {
        try {
            CtClass ctClass = pool.get(className);
            var classCFG = new ClassCFG();
            classesCFG.put(className, classCFG);

            for (CtMethod method : ctClass.getMethods()) {
                classCFG.addMethodCFG(createMethodCFG(method), method.getName(), method.getMethodInfo().getDescriptor());
            }
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

    private ControlFlow.Block[] createMethodCFG(CtMethod method) throws BadBytecode {

        MethodInfo methodInfo = method.getMethodInfo();
        ControlFlow controlFlow = new ControlFlow(method.getDeclaringClass(), methodInfo);

        return controlFlow.basicBlocks();

    }

}
