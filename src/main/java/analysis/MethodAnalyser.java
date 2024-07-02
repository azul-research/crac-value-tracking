package analysis;

import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.analysis.ControlFlow;
import output.CurrentState;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static analysis.InstructionsMatcher.*;
import static analysis.InstructionsMatcher.matchReturn;

public class MethodAnalyser {

    Map<Integer, ControlFlow.Block> methodCFG;

    ControlFlow.Block[] blocks;

    Map<Integer, CurrentState> currentBlocksStates;
    //    CtMethod method;
    CodeAttribute codeAttribute;
    int startBlock;
    int endBlock;

    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};



    public MethodAnalyser(ControlFlow.Block[] blocks, CtMethod method) {
        this.blocks = blocks;

        this.methodCFG = new HashMap<>();
        for (var block : blocks) {
            this.methodCFG.put(block.position(), block);
        }

        codeAttribute = method.getMethodInfo().getCodeAttribute();
        this.startBlock = Collections.min(this.methodCFG.keySet());
        this.endBlock = Collections.max(this.methodCFG.keySet());
        System.out.println("Start block: " + startBlock + ", end block: " + endBlock);

        currentBlocksStates = new HashMap<>();
        for (var index : methodCFG.keySet()) {
            currentBlocksStates.put(index, CurrentState.getEmptyState());
        }
    }

    public void analyse() {

        CurrentState previousState;
        do {
            previousState = currentBlocksStates.get(endBlock);
            for (var block: blocks) {
                analyseBasicBlock(block.position());
            }
        } while (previousState.equals(currentBlocksStates.get(endBlock)));

        System.out.println("The result is: " + currentBlocksStates.get(endBlock));
    }

    void analyseBasicBlock(int blockIndex) {
        System.out.println("Analysing block: " + blockIndex);
        CurrentState state;
        if (blockIndex == startBlock) {
            state = CurrentState.getEmptyState();
            state.addVariable(0);
        } else {
            state = getStartingState(blockIndex);
        }
        analyseCode(state, blockIndex);

        currentBlocksStates.put(blockIndex, state);

//        var block = methodCFG.get(blockIndex);

//        for (int i = 0; i < block.exits(); i++) {
//            analyseBasicBlock(block.exit(i).position());
//        }


    }

    void analyseCode(CurrentState state, int blockIndex) {

        int blockEnd = methodCFG.get(blockIndex).length() + blockIndex;
        var code = codeAttribute.getCode();
        var iterator = codeAttribute.iterator();
        int index = blockIndex;

        Stack<Boolean> stack = new Stack<>();
        while (index < blockEnd) {
            int opcode = iterator.byteAt(index);
            String name =  Mnemonic.OPCODE[opcode];
            System.out.println("Instruction at index " + index + ": " + name);

            if (matchConstLoad(name)) {
                stack.add(true);
            } else if (matchConstLoadFromPool(name)) {
                stack.add(true);
            } else if (matchStoreData(name)) {
                var varNumber = getNumberInOpCode(name);
                if (!stack.pop()) {
                    state.addVariable(varNumber);
                }
            } else if (matchStoreToVariable(name)) {
                int varNumber = parseNextByte(iterator, index);
                if (!stack.pop()) {
                    state.addVariable(varNumber);
                }
            }
            else if (matchLoadVariable(name)) {
                var varNumber = getNumberInOpCode(name);
                stack.add(!state.containsVariable(varNumber));
            } else if (matchBinOperation(name)) {
                var first = stack.pop();
                var second = stack.pop();
                stack.add(first && second);
            } else if (matchLoadArrayElem(name)) {
                stack.pop();
                var isSafe = stack.pop();
                stack.add(isSafe);
            }
            else if (!matchReturn(name)) {
                System.out.println("UNKNOWN OPCODE: " + name);
            }

            index += opcodeLength[opcode];


        }


    }



    CurrentState getStartingState(int index) {
        var block = methodCFG.get(index);
        int num = block.incomings();
        List<ControlFlow.Block> predecessors = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            predecessors.add(block.incoming(i));
        }

        return mergeCurStates(predecessors);
    }

    CurrentState mergeCurStates(List<ControlFlow.Block> blocks) {
        if (blocks.isEmpty()) {
            return CurrentState.getEmptyState();
        }
        CurrentState state = CurrentState.getEmptyState();
        for (var block : blocks) {
            state.mergeWith(currentBlocksStates.get(block.position()));
        }
        return state;
    }

    int getNumberInOpCode(String opCode) {
        String regex = ".*_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }


    int parseNextByte(CodeIterator iterator, int index) {
            return iterator.byteAt(index + 1) & 0xff;

    };

}
