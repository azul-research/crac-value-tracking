package analysis;

import javassist.bytecode.analysis.ControlFlow;
import output.CurrentState;

import java.util.Collections;
import java.util.Map;

public class MethodAnalyser {

    Map<Integer, ControlFlow.Block> methodCFG;
    int startBlock;
    int endBlock;


    public MethodAnalyser( Map<Integer, ControlFlow.Block> methodCFG) {
        this.methodCFG = methodCFG;
        this.startBlock = Collections.min(methodCFG.keySet());
        this.endBlock = Collections.max(methodCFG.keySet());
        System.out.println("Start block: " + startBlock + ", end block: " + endBlock);
    }

    void analyseMainMethod(Map<Integer, ControlFlow.Block> methodCFG) {


    }


    void analyseBasicBlock(ControlFlow.Block block) {
        CurrentState state;
        if (!basicBlockContainsPredecessors(block)) {
            state = CurrentState.getEmptyState();
        }
        else {

        }

    }


    boolean basicBlockContainsPredecessors(ControlFlow.Block block) {
        return block.incomings() != 0;
    }
}
