package analysis;

import entitites.*;
import entitites.derivatives.OperationDerivative;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.*;
import javassist.bytecode.analysis.ControlFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.CurrentState;

import java.util.*;

import static analysis.InstructionsMatcher.*;
import static entitites.Entity.createNonDerivative;

public class MethodAnalyser {
    private static final Logger logger = LogManager.getLogger(MethodAnalyser.class);
    Map<Integer, ControlFlow.Block> methodCFG;

    ControlFlow.Block[] blocks;

    Map<Integer, CurrentState> currentBlocksStates;
    //    CtMethod method;
    CodeAttribute codeAttribute;

    InstructionsProcessor processor;
    int startBlock;
    int endBlock;
    int numOfVars;
    String fileName;
    Optional<Entity> thisObj;

    Analyser mainAnalyser;
    CtBehavior method;

    Map<Integer, String> startingDerivatives;
    Set<String> startingLoadedClasses;
    Map<String, Map<String, Entity>> startingInitializedClasses;

    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};

    public MethodAnalyser(ControlFlow.Block[] cfgBlocks, CtBehavior method, List<Integer> derivativeVars, Analyser mainAnalyser, Optional<Entity> thisObject, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {

        this.thisObj = thisObject;
        this.mainAnalyser = mainAnalyser;
        this.codeAttribute = method.getMethodInfo().getCodeAttribute();
        this.method = method;
        setFileName(method);

        setStartingDerivatives(derivativeVars);
        setMethodCFG(cfgBlocks);

        this.blocks = cfgBlocks;
        this.startBlock = Collections.min(this.methodCFG.keySet());
        this.endBlock = Collections.max(this.methodCFG.keySet());

        logger.debug("Start block: {}, end block: {}", startBlock, endBlock);

        this.numOfVars = codeAttribute.getMaxLocals();
        this.startingLoadedClasses = loadedClasses;
        this.startingInitializedClasses = initialisedClasses;
        setCurrentBlocksStates(numOfVars, this.startingDerivatives, loadedClasses, initialisedClasses);

        this.processor = new InstructionsProcessor(this, method);

        logger.debug("Number of local variables: {}", numOfVars);

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


    public Analyser getMainAnalyser() {
        return mainAnalyser;
    }

    private void setFileName(CtBehavior method) {
        var sourceFileAttribute = (SourceFileAttribute) method.getDeclaringClass().getClassFile().getAttribute(SourceFileAttribute.tag);
        this.fileName = sourceFileAttribute.getFileName();
    }

    private void setMethodCFG(ControlFlow.Block[] cfgBlocks) {
        this.methodCFG = new HashMap<>();
        for (var block : cfgBlocks) {
            this.methodCFG.put(block.position(), block);
        }
    }

    private void setCurrentBlocksStates(int numOfVars, Map<Integer, String> derivativeVars, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        currentBlocksStates = new HashMap<>();
        for (var index : methodCFG.keySet()) {
            currentBlocksStates.put(index, CurrentState.getEmptyState(numOfVars, derivativeVars, loadedClasses, initialisedClasses));
        }
    }

    private void setStartingDerivatives(List<Integer> derivativeVar) {
        this.startingDerivatives = new HashMap<>();
        for (var derivative : derivativeVar) {
            startingDerivatives.put(derivative, getVarName(derivative));
        }
    }

    public void analyse() {
        logger.debug("Analysing method: {}", method.getName());
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

        var finalState = currentBlocksStates.get(endBlock);
        logFinalState(finalState);

    }

    private void logFinalState(CurrentState finalState) {
        logger.info("Variables at the end of the method:");
        for (int i = 0; i < numOfVars; i++) {
            logger.info("{} - {}", getVarName(i), finalState.getVarValue(i).info(0));
        }

        logger.info("Static fields at the end of the method:");
        for (var cl : finalState.getInitialisedClasses().entrySet()) {
            logger.info("Fields of class {}", cl.getKey());
            for (var field : cl.getValue().entrySet()) {
                logger.info("{} - {}", field.getKey(), field.getValue().info(0));

            }
        }
    }

    void analyseBasicBlock(int blockIndex) {
        logger.debug("Analysing block: {}", blockIndex);
        CurrentState state;
        if (blockIndex == startBlock) {
            state = CurrentState.getEmptyState(numOfVars, startingDerivatives, startingLoadedClasses, startingInitializedClasses);
        } else {
            state = getStartingState(blockIndex);
        }
        analyseCode(state, blockIndex);

        currentBlocksStates.put(blockIndex, state);

    }

    void analyseCode(CurrentState state, int blockIndex) {
        int blockEnd = methodCFG.get(blockIndex).length() + blockIndex;
        var iterator = codeAttribute.iterator();
        int index = blockIndex;

        ArrayDeque<Entity> stack = state.getStack();

        while (index < blockEnd) {
            int opcode = iterator.byteAt(index);
            String name = Mnemonic.OPCODE[opcode];
            logger.debug("{}:{} instruction:{} {}", fileName, getLineNumber(index), index, name);

            if (matchConstLoad(name) || matchConstLoadFromPool(name) || matchByteLoad(name)) {
                stack.push(createNonDerivative());

            } else if (matchStoreData(name)) {
                var varNumber = getNumberInOpCode(name);
                processor.processDataStore(index, stack, state, varNumber);

            } else if (matchStoreToVariable(name)) {
                int varNumber = parseNextBytes(iterator, index, 1);
                processor.processDataStore(index, stack, state, varNumber);

            } else if (matchLoadVariable(name)) {
                var varNumber = getNumberInOpCode(name);
                stack.push(state.getVarValue(varNumber));

            } else if (matchLoadArrayElem(name)) {
                var arrayIndex = stack.pop();
                var valueOnStack = stack.pop();
                stack.push(valueOnStack);

            } else if (matchBinOperation(name)) {
                processor.processBinOperation(index, stack);

            } else if (matchCreateArray(name)) {
                processor.processCreateArray(index, stack, iterator);

            } else if (matchStoreToArray(name)) {
                processor.processStoreToArray(index, stack);

            } else if (matchIncrementLocal(name)) {
                processor.processIncrementLocal(index, state, iterator);

            } else if (matchInvokeVirtual(name) | matchInvokeSpecial(name)) {
                var methodIndex = parseNextBytes(iterator, index, 2);
                processor.processInvokeMethod(methodIndex, stack, state, false);

            } else if (matchInvokeStatic(name)) {
                var methodIndex = parseNextBytes(iterator, index, 2);
                processor.processInvokeMethod(methodIndex, stack, state, true);
            } else if (matchReturnVoid(name)) {
                state.setEmptyReturn();

            } else if (matchReturnValue(name)) {
                state.updateReturnState(stack.pop());

            } else if (matchGetField(name)) {
                var objectRef = stack.pop();
                stack.push(createEntitySuccessor(objectRef, getLineNumber(index)));
            } else if (matchPutField(name)) {
                processor.processPutField(index, stack);
            } else if (matchGetStatic(name)) {
                int indexInConstPool = parseNextBytes(iterator, index, 2);
                Entity staticField = processor.processGetStatic(indexInConstPool, state);
                stack.push(staticField);

            } else if (matchPutStatic(name)) {
                int indexInConstPool = parseNextBytes(iterator, index, 2);
                processor.processPutStatic(indexInConstPool, stack.pop(), state, getLineNumber(index));
            } else if (matchGetArrayLength(name)) {
                stack.push(createNonDerivative());
            } else if (matchNew(name)) {
                stack.push(createNonDerivative());
            } else if (matchDuplicateValue(name)) {
                stack.push(stack.getFirst());
            } else if (!matchIf(name) && !matchGoto(name)) {
                logger.warn("UNKNOWN OPCODE: {}", name);
            }

            index += opcodeLength[opcode];

        }
    }


    private Entity createdUndefined() {
        return new Entity(Entity.Type.UNDEFINED);
    }


    Entity createMergedEntity(Entity first, Entity second, int codeLine) {
        if (first.compareType(second) > 0) {
            return first;
        } else if (first.compareType(second) < 0) {
            return second;
        } else if (first.compareType(second) == 0 && first.isDerivativeSet()) {
            return createMergedDerivativeSet(first, second, codeLine);
        } else {
            return first;
        }
    }

    Entity createMergedDerivativeSet(Entity first, Entity second, int codeLine) {
        assert first.isDerivativeSet();
        assert second.isDerivativeSet();

        Entity result = new Entity(Entity.Type.DERIVATIVE_SET);

        for (var pred1 : first.getDerivativeSet()) {
            for (var pred2 : second.getDerivativeSet()) {
                result.addDerivative(new OperationDerivative(codeLine, pred1, pred2));
            }
        }
        return result;
    }


    Entity createEntitySuccessor(Entity oldValue, int line) {
        if (!oldValue.isDerivativeSet()) {
            return oldValue;
        }
        Entity newValue = new Entity(Entity.Type.DERIVATIVE_SET);

        for (var pred : oldValue.getDerivativeSet()) {
            newValue.addDerivative(new OperationDerivative(line, pred));
        }
        return newValue;
    }

    private CurrentState getStartingState(int index) {
        var block = methodCFG.get(index);
        int num = block.incomings();
        List<CurrentState> predecessors = new ArrayList<>();
        int blockIndex;
        for (int i = 0; i < num; i++) {
            blockIndex = block.incoming(i).position();
            predecessors.add(currentBlocksStates.get(blockIndex));
        }
        return mergeCurrentStates(predecessors);
    }

    private CurrentState mergeCurrentStates(List<CurrentState> currentStates) {
        if (currentStates.size() == 1) {
            return new CurrentState(currentStates.getFirst());
        }

        CurrentState curStateResult = CurrentState.getEmptyState(numOfVars, startingDerivatives, startingLoadedClasses, startingInitializedClasses);
        if (currentStates.isEmpty()) {
            return curStateResult;
        }

        if (!currentStates.getFirst().getStack().isEmpty()) {
            curStateResult.updateStack(mergeStacks(currentStates));
        }

        for (int i = 0; i < numOfVars; i++) {
            curStateResult.updateVariable(i, mergeEntitiesOfVariable(currentStates, i));
        }

        return curStateResult;
    }

    private ArrayDeque<Entity> mergeStacks(List<CurrentState> currentStates) {
        int stackSize = currentStates.getFirst().getStack().size();
        Entity[] result = new Entity[stackSize];

        for (int i = 0; i < stackSize; i++) {
            Entity newEntity = new Entity(Entity.Type.UNDEFINED);

            for (var state : currentStates) {
                Entity entity = state.getStack().toArray(Entity[]::new)[i];
                if (newEntity.compareType(entity) < 0) {
                    newEntity = entity;
                } else if (newEntity.compareType(entity) == 0) {
                    if (newEntity.isDerivativeSet()) {
                        newEntity.mergeWithDerivativeSet(entity);
                    }
                }
            }
            result[i] = newEntity;
        }
        return new ArrayDeque<>(Arrays.asList(result));
    }

    Entity mergeEntitiesOfVariable(List<CurrentState> currentStates, int varNumber) {
        Entity newEntity = new Entity(Entity.Type.UNDEFINED);

        for (var state : currentStates) {
            Entity entity = state.getVarValue(varNumber);
            if (newEntity.compareType(entity) < 0) {
                newEntity = entity;
            } else if (newEntity.compareType(entity) == 0) {
                if (newEntity.isDerivativeSet()) {
                    newEntity.mergeWithDerivativeSet(entity);
                }
            }
        }
        return newEntity;
    }


    int parseNextBytes(CodeIterator iterator, int index, int bytesNumber) {
        int result = 0;
        for (int i = 0; i < bytesNumber; i++) {
            int indexbyte = (iterator.byteAt(i + index + 1) & 0xff);
            result = result | (indexbyte << (bytesNumber - i - 1) * 8);
        }
        return result;
    }


}
