package input;

import javassist.bytecode.analysis.ControlFlow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassControlFlowGraph {

    private Map<String, Map<Integer, ControlFlow.Block>> methodsCFG = new HashMap<>();


    void addMethodCFG(Map<Integer, ControlFlow.Block> basicBlocks, String name) {
        methodsCFG.put(name, basicBlocks);

    }
}
