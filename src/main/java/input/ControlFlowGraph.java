package input;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.*;

import java.util.*;

public class ControlFlowGraph {

    private ClassPool pool;
    private Map<String, ClassCFG> classesCFG = new HashMap<>();


    public Map<String, ClassCFG> getCFG() {
        return classesCFG;
    }

    public ClassCFG getClassCFG(String name) {
        return classesCFG.get(name);
    }

    public ControlFlowGraph(String jarFilePath) {
        try {
            pool = ClassPool.getDefault();
            pool.appendClassPath(jarFilePath);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void createClassCFG(String className) {
        try {
            CtClass ctClass = pool.get(className);
            var classCFG = new ClassCFG();
            classesCFG.put(className, classCFG);

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                System.out.println("Analyzing method: " + method.getName());
                classCFG.addMethodCFG(createMethodCFG(method), method.getName());
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        } catch (BadBytecode e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public CtMethod getMethod(String className, String methodName) {
        try {
            CtClass ctClass = pool.get(className);
            return ctClass.getDeclaredMethod(methodName);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static ControlFlow.Block[] createMethodCFG(CtMethod method) throws BadBytecode {

        MethodInfo methodInfo = method.getMethodInfo();
        ControlFlow controlFlow = new ControlFlow(method.getDeclaringClass(), methodInfo);
        ControlFlow.Block[] blocks = controlFlow.basicBlocks();

        for (var block : blocks) {
            System.out.println(block.toString());
        }
        return blocks;

    }

}
