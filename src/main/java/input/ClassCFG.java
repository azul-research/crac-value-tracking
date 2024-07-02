package input;

import javassist.bytecode.analysis.ControlFlow;

import java.util.HashMap;
import java.util.Map;

public class ClassCFG {

    private Map<String, ControlFlow.Block[]> methodsCFG = new HashMap<>();


    void addMethodCFG(ControlFlow.Block[] basicBlocks, String name) {
        methodsCFG.put(name, basicBlocks);
    }

    public ControlFlow.Block[] getMethodCFG(String methodName) {
        return methodsCFG.get(methodName);
    }
}
