package input;

import javassist.bytecode.analysis.ControlFlow;

import java.util.HashMap;
import java.util.Map;

public class ClassCFG {

    private Map<String, Map<Integer, ControlFlow.Block>> methodsCFG = new HashMap<>();


    void addMethodCFG(Map<Integer, ControlFlow.Block> basicBlocks, String name) {
        methodsCFG.put(name, basicBlocks);
    }

    public Map<Integer, ControlFlow.Block> getMethodCFG(String methodName) {
        return methodsCFG.get(methodName);
    }
}
