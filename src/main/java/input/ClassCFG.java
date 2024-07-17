package input;

import entitites.Entity;
import javassist.bytecode.analysis.ControlFlow;

import java.util.HashMap;
import java.util.Map;

public class ClassCFG {

    private final Map<MethodDescription, ControlFlow.Block[]> methodsCFG = new HashMap<>();


    public record MethodDescription(String name, String desc) {}

    boolean containsMethod(String name, String desc) {
        return methodsCFG.containsKey(new MethodDescription(name, desc));
    }

    void addMethodCFG(ControlFlow.Block[] basicBlocks, String name, String desc) {
        methodsCFG.put(new MethodDescription(name, desc), basicBlocks);
    }

    public ControlFlow.Block[] getMethodCFG(String methodName, String desc) {
        return methodsCFG.get(new MethodDescription(methodName, desc));
    }
}
