package input;

import entitites.Entity;
import javassist.bytecode.analysis.ControlFlow;

import java.util.HashMap;
import java.util.Map;

public class ClassCFG {

    private final Map<Map.Entry<String, String>, ControlFlow.Block[]> methodsCFG = new HashMap<>();


    void addMethodCFG(ControlFlow.Block[] basicBlocks, String name, String desc) {
        methodsCFG.put(Map.entry(name, desc), basicBlocks);
    }

    public ControlFlow.Block[] getMethodCFG(String methodName, String desc) {
        return methodsCFG.get(Map.entry(methodName, desc));
    }
}
