package input.cfg;

import java.util.ArrayList;
import java.util.List;

import static input.cfg.ControlFlowGraph.getOpCode;

public class BasicBlock {

    private List<BasicBlock> predecessors = new ArrayList<>();

    private List<BasicBlock> successors = new ArrayList<>();

    byte[] basicBlockCode;

    int startLine;
    int endLine;
    byte[] code;

    public BasicBlock(int startLine, int endLine, byte[] code) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.code = code;
    }


    public String getLastInstruction() {
        if (startLine == endLine) {
            return getOpCode(code, 0);
        }
        return getOpCode(code, code.length - 3);
    }

    public void addSuccessor(BasicBlock block) {
        successors.add(block);
    }

    public void addPredecessor(BasicBlock block) {
        predecessors.add(block);
    }

    public int getEndLine() {
        return endLine;
    }
}
