package analysis;

import entitites.*;
import entitites.derivatives.DerivativeEntity;
import entitites.derivatives.PhiDerivative;
import entitites.derivatives.StraightDerivative;
import javassist.CtMethod;
import javassist.bytecode.*;
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
    int numOfVars;
    String fileName;

    Map<Integer, String> startingDerivatives;

    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};

    public MethodAnalyser(ControlFlow.Block[] cfgBlocks, CtMethod method, Set<Integer> derivativeVars) {

        this.codeAttribute = method.getMethodInfo().getCodeAttribute();
        setFileName(method);

        setStartingDerivatives(derivativeVars);
        setMethodCFG(cfgBlocks);

        this.blocks = cfgBlocks;
        this.startBlock = Collections.min(this.methodCFG.keySet());
        this.endBlock = Collections.max(this.methodCFG.keySet());

        System.out.println("Start block: " + startBlock + ", end block: " + endBlock);

        this.numOfVars = codeAttribute.getMaxLocals();

        setCurrentBlocksStates(numOfVars, this.startingDerivatives);

        System.out.println("Number of local variables: " + numOfVars);

    }

    public CurrentState getAnalysisResult() {
        return currentBlocksStates.get(endBlock);
    }

    String getVarName(int i) {
        var attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        return attribute.variableName(i);
    }

    Integer getLineNumber(int i) { // i - number of bytecode instruction in method
        var attribute = (LineNumberAttribute) codeAttribute.getAttribute(LineNumberAttribute.tag);
        return attribute.toLineNumber(i);

    }

    private void setFileName(CtMethod method) {
        var sourceFileAttribute = (SourceFileAttribute) method.getDeclaringClass().getClassFile().getAttribute(SourceFileAttribute.tag);
        this.fileName = sourceFileAttribute.getFileName();
    }

    private void setMethodCFG(ControlFlow.Block[] cfgBlocks) {
        this.methodCFG = new HashMap<>();
        for (var block : cfgBlocks) {
            this.methodCFG.put(block.position(), block);
        }
    }

    private void setCurrentBlocksStates(int numOfVars, Map<Integer, String> derivativeVars) {
        currentBlocksStates = new HashMap<>();
        for (var index : methodCFG.keySet()) {
            currentBlocksStates.put(index, CurrentState.getEmptyState(numOfVars, derivativeVars));
        }
    }

    private void setStartingDerivatives(Set<Integer> derivativeVar) {
        this.startingDerivatives = new HashMap<>();
        var iter = derivativeVar.iterator();
        int index;
        while (iter.hasNext()) {
            index = iter.next();
            startingDerivatives.put(index, getVarName(index));
        }

    }


    public void analyse() {

        for (var block : blocks) {
            analyseBasicBlock(block.position());
        }


        CurrentState previousState;
        do {
            previousState = currentBlocksStates.get(endBlock);
            for (var block : blocks) {
                if (block.position() != startBlock) {
                    analyseBasicBlock(block.position());
                }
            }
        } while (!previousState.equals(currentBlocksStates.get(endBlock)));


        System.out.println();
        System.out.println("Variables at the end of the method:");
        for (int i = 0; i < numOfVars; i++) {
            System.out.print(getVarName(i) + " - ");
            System.out.println(currentBlocksStates.get(endBlock).getVarValue(i).info(0));
        }

    }

    void analyseBasicBlock(int blockIndex) {
        System.out.println("Analysing block: " + blockIndex);
        CurrentState state;
        if (blockIndex == startBlock) {
            state = CurrentState.getEmptyState(numOfVars, startingDerivatives);
        } else {
            state = getStartingState(blockIndex);
        }
        analyseCode(state, blockIndex);

        currentBlocksStates.put(blockIndex, state);

    }

    void analyseCode(CurrentState state, int blockIndex) {

        int blockEnd = methodCFG.get(blockIndex).length() + blockIndex;
//        var code = codeAttribute.getCode();
        var iterator = codeAttribute.iterator();
        int index = blockIndex;

        ArrayDeque<Entity> stack = new ArrayDeque<>();

        while (index < blockEnd) {
            int opcode = iterator.byteAt(index);
            String name = Mnemonic.OPCODE[opcode];
            System.out.println(fileName + ":" + getLineNumber(index));
            System.out.println("Instruction at index " + index + ": " + name);

            if (matchConstLoad(name) || matchConstLoadFromPool(name)) {
                stack.push(createNonDerivative(getLineNumber(index)));

            } else if (matchStoreData(name)) {
                var varNumber = getNumberInOpCode(name);
                processDataStore(index, stack, state, varNumber);

            } else if (matchStoreToVariable(name)) {
                int varNumber = parseNextByte(iterator, index);
                processDataStore(index, stack, state, varNumber);

            } else if (matchLoadVariable(name)) {
                var varNumber = getNumberInOpCode(name);
                stack.push(state.getVarValue(varNumber));

            } else if (matchLoadArrayElem(name)) {
                var arrayIndex = stack.pop();
                var valueOnStack = stack.pop();
                stack.push(valueOnStack);
//            } else if (matchBinOperation(name)) {
//                var first = stack.pop();
//                var second = stack.pop();
//
//                stack.push(createMergedSuccessor(first, second));
            } else if (!matchReturn(name) && !matchIf(name) && !matchGoto(name)) {
                System.out.println("UNKNOWN OPCODE: " + name);
            }

            index += opcodeLength[opcode];

        }
    }


    void processDataStore(int index, ArrayDeque<Entity> stack, CurrentState state, int varNumber) {
        var valueOnStack = stack.pop();
        if (valueOnStack.isDerivative()) {
            state.updateVariable(varNumber, createSuccessor(valueOnStack, getLineNumber(index)));
        } else {
            state.updateVariable(varNumber, createNonDerivative(getLineNumber(index)));
        }
    }

    Entity createNonDerivative(int line) {
        return new NonDerivativeEntity(line);
    }

    DerivativeEntity createSuccessor(Entity oldValue, int line) {
        return new StraightDerivative(line, oldValue);
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

    private CurrentState mergeCurStates(List<ControlFlow.Block> blocks) {

        if (blocks.size() == 1) {
            return new CurrentState(currentBlocksStates.get(blocks.getFirst().position()));
        }
        CurrentState state = CurrentState.getEmptyState(numOfVars, startingDerivatives);

        if (blocks.isEmpty()) {
            return state;
        }

        for (int i = 0; i < numOfVars; i++) {
            ArrayList<Entity> predecessors = new ArrayList<>();
            Set<Entity> predecessorsSet = new HashSet<>();

            for (var bl : blocks) {
                Entity entity = currentBlocksStates.get(bl.position()).getVarValue(i);
                if (!entity.isUndefined() && !predecessorsSet.contains(entity)) {
                    predecessorsSet.add(entity);
                    predecessors.add(entity);
                }

                if (predecessors.size() == 1) {
                    state.updateVariable(i, predecessors.getFirst());
                } else {
                    state.updateVariable(i, new PhiDerivative(0, predecessors.toArray(Entity[]::new)));
                }
            }
        }

        return state;
    }

    private int getNumberInOpCode(String opCode) {
        String regex = ".*_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }


    private int parseNextByte(CodeIterator iterator, int index) {
        return iterator.byteAt(index + 1) & 0xff;
    }


}
