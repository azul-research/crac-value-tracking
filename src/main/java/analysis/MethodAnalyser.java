package analysis;

import entitites.*;
import entitites.derivatives.DerivativeEntity;
import entitites.derivatives.OpDerivative;
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

    public MethodAnalyser(ControlFlow.Block[] cfgBlocks, CtMethod method, List<Integer> derivativeVars) {

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
        return attribute.variableNameByIndex(i);
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

    private void setStartingDerivatives(List<Integer> derivativeVar) {
        this.startingDerivatives = new HashMap<>();
        for (var derivative : derivativeVar) {
            startingDerivatives.put(derivative, getVarName(derivative));
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
                int varNumber = parseNextBytes(iterator, index, 1);
                processDataStore(index, stack, state, varNumber);

            } else if (matchLoadVariable(name)) {
                var varNumber = getNumberInOpCode(name);
                stack.push(state.getVarValue(varNumber));

            } else if (matchLoadArrayElem(name)) {
                var arrayIndex = stack.pop();
                var valueOnStack = stack.pop();
                stack.push(valueOnStack);
            } else if (matchBinOperation(name)) {
                var first = stack.pop();
                var second = stack.pop();
                stack.push(createMergedSuccessor(first, second, getLineNumber(index)));
            } else if (matchCreateArray(name)) {
                processCreateArray(index, stack, iterator);
            } else if (matchStoreToArray(name)) {
                var value = stack.pop();
                var ind = stack.pop();
                var arrayRef = stack.pop();
//                processStoreToArray(index);
            } else if (matchIncrementLocal(name)) {
                var varIndex = parseNextBytes(iterator, index, 1);
                if (state.isDerivative(varIndex)) {
                    state.updateVariable(varIndex, createSuccessor(state.getVarValue(index), getLineNumber(index)));
                } else {
                    state.updateVariable(varIndex, createNonDerivative(getLineNumber(index)));
                }
            } else if (!matchReturn(name) && !matchIf(name) && !matchGoto(name)) {
                System.out.println("UNKNOWN OPCODE: " + name);
            }

            index += opcodeLength[opcode];

        }
    }

    private void processStoreToArray(int index) {

    }

    private void processCreateArray(int index, ArrayDeque<Entity> stack, CodeIterator iterator) {
        var count = stack.pop();
        var typeRef = parseNextBytes(iterator, index, 2);
        if (count.isDerivative()) {
            stack.push(createSuccessor(count, getLineNumber(index)));
        } else {
            stack.push(createNonDerivative(getLineNumber(index)));
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

    private Entity createMergedSuccessor(Entity first, Entity second, int codeLine) {
        if (first.isNonDerivative() && second.isNonDerivative()) {
            return new NonDerivativeEntity(codeLine);
        } else if (first.isDerivative() && second.isDerivative()) {
            return new OpDerivative(codeLine, first, second);
        } else if (first.isDerivative()) {
            return createSuccessor(first, codeLine);
        } else {
            return createSuccessor(second, codeLine);
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
                if (entity.isNonDerivative() && predecessorsSet.stream().anyMatch(Entity::isNonDerivative)) {
                    continue;
                }
                if (!entity.isUndefined() && !setContainsEntity(predecessorsSet, entity)) {
                    predecessorsSet.add(entity);
                    predecessors.add(entity);
                }
            }

            if (predecessors.size() == 1) {
                state.updateVariable(i, predecessors.getFirst());
            } else {
                state.updateVariable(i, new PhiDerivative(0, predecessors.toArray(Entity[]::new)));
            }
        }

        return state;
    }


    boolean setContainsEntity(Set<Entity> set, Entity entity) {
        if (set.contains(entity)) {
            return true;
        }
        var predecessors = set.toArray(Entity[]::new);
        for (var pred : predecessors) {
            if (pred.isDerivative()) {
                if (((DerivativeEntity) pred).containsEntity(entity)) {
                    return true;
                }
            }
        }

        return false;

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


    private int parseNextBytes(CodeIterator iterator, int index, int bytesNumber) {
        int result = 1;
        for (int i = index; i < bytesNumber + index; i++) {
            result = result | ((iterator.byteAt(i + 1) & 0xff) << (bytesNumber - (index - i) - 1) * 8);
        }

        return result;
    }


}
