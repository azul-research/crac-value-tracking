package analysis;

import entitites.*;
//import entitites.DerivativeSet;
import entitites.derivatives.OperationDerivative;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.*;
import javassist.bytecode.analysis.ControlFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.CurrentState;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static analysis.InstructionsMatcher.*;

public class MethodAnalyser {

    private static final Logger logger = LogManager.getLogger(MethodAnalyser.class);


    Map<Integer, ControlFlow.Block> methodCFG;

    ControlFlow.Block[] blocks;

    Map<Integer, CurrentState> currentBlocksStates;
    //    CtMethod method;
    CodeAttribute codeAttribute;
    int startBlock;
    int endBlock;
    int numOfVars;
    String fileName;
    Entity thisObj;

    Analyser mainAnalyser;
    CtMethod method;

    Map<Integer, String> startingDerivatives;

    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};

    public MethodAnalyser(ControlFlow.Block[] cfgBlocks, CtMethod method, List<Integer> derivativeVars, Analyser mainAnalyser, Entity thisObject) {

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

        setCurrentBlocksStates(numOfVars, this.startingDerivatives);

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

        logger.info("Variables at the end of the method:");
        for (int i = 0; i < numOfVars; i++) {
            logger.info("{} - {}", getVarName(i), currentBlocksStates.get(endBlock).getVarValue(i).info(0));
        }

    }

    void analyseBasicBlock(int blockIndex) {
        logger.debug("Analysing block: {}", blockIndex);
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

        ArrayDeque<Entity> stack = state.getStack();

        while (index < blockEnd) {
            int opcode = iterator.byteAt(index);
            String name = Mnemonic.OPCODE[opcode];
            logger.debug("{}:{} instruction:{} {}", fileName, getLineNumber(index), index, name);

            if (matchConstLoad(name) || matchConstLoadFromPool(name) || matchByteLoad(name)) {
                stack.push(createNonDerivative());

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
                stack.push(createMergedEntity(first, second, getLineNumber(index)));

            } else if (matchCreateArray(name)) {
                processCreateArray(index, stack, iterator);

            } else if (matchStoreToArray(name)) {
                processStoreToArray(index, stack);

            } else if (matchIncrementLocal(name)) {
                processIncrementLocal(index, state, iterator);

            } else if (matchInvokeVirtual(name) | matchInvokeSpecial(name)) {
                var methodIndex = parseNextBytes(iterator, index, 2);
                analyseAnotherMethod(methodIndex, stack);

            } else if (matchInvokeStatic(name)) {
                var methodIndex = parseNextBytes(iterator, index, 2);
                analyseAnotherMethod(methodIndex, stack);

            } else if (matchReturnVoid(name)) {
                state.updateReturnState(createdUndefined());

            } else if (matchReturnValue(name)) {
                state.updateReturnState(stack.pop());

            } else if (matchGetField(name)) {
                var value = stack.pop();
                if (value.isDerivativeSet()) {
                    stack.push(createDerivativeSuccessor(value, getLineNumber(index)));
                } else {
                    stack.push(createNonDerivative());
                }

            } else if (matchGetStatic(name)) {
                // TODO
                // symbolic reference
                stack.push(createNonDerivative());

            } else if (matchGetArrayLength(name)) {
                stack.push(createNonDerivative());
            } else if (!matchIf(name) && !matchGoto(name)) {
                logger.warn("UNKNOWN OPCODE: {}", name);
            }

            index += opcodeLength[opcode];

        }
    }

    private Entity createdUndefined() {
        return new Entity(Entity.Type.UNDEFINED);
    }

    private void processIncrementLocal(int index, CurrentState state, CodeIterator iterator) {
        var varIndex = parseNextBytes(iterator, index, 1);
        if (state.isDerivative(varIndex)) {
            state.updateVariable(varIndex, createDerivativeSuccessor(state.getVarValue(index), getLineNumber(index)));
        } else {
            state.updateVariable(varIndex, createNonDerivative());
        }
    }

    private void processStoreToArray(int index, ArrayDeque<Entity> stack) {
        var value = stack.pop();
        var ind = stack.pop();
        var arrayRef = stack.pop();

        if (value.isDerivativeSet()) {
            if (arrayRef.isNonDerivative()) {
                arrayRef.setToDerivative();
                for (var derivative : value.getDerivativeSet()) {
                    arrayRef.addDerivative(new OperationDerivative(getLineNumber(index), derivative));
                }
            }
        }

    }

    private void processCreateArray(int index, ArrayDeque<Entity> stack, CodeIterator iterator) {
        var count = stack.pop();
        var typeRef = parseNextBytes(iterator, index, 2);
        if (count.isDerivativeSet()) {
            stack.push(createDerivativeSuccessor(count, getLineNumber(index)));
        } else {
            stack.push(createNonDerivative());
        }
    }


    void processDataStore(int index, ArrayDeque<Entity> stack, CurrentState state, int varNumber) {
        var valueOnStack = stack.pop();
        if (valueOnStack.isDerivativeSet()) {
            state.updateVariable(varNumber, createDerivativeSuccessor(valueOnStack, getLineNumber(index)));
        } else {
            state.updateVariable(varNumber, valueOnStack);
        }
    }

    private Entity createMergedEntity(Entity first, Entity second, int codeLine) {
        if (first.compareType(second) > 0) {
            return first;
        } else if (first.compareType(second) < 0) {
            return second;
        } else if (first.compareType(second) == 0 && first.isDerivativeSet()) {
            return createMergedDerivative(first, second, codeLine);
        } else {
            return first;
        }
    }


    private Entity createMergedDerivative(Entity first, Entity second, int codeLine) {
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

    private void analyseAnotherMethod(int index, ArrayDeque<Entity> stack) {

        var constPool = method.getDeclaringClass().getClassFile().getConstPool();

        int methodRefIndex = constPool.getMethodrefClass(index);
        String className = constPool.getClassInfo(methodRefIndex);
        String methodName = constPool.getMethodrefName(index);
        String methodDescriptor = constPool.getMethodrefType(index);

        logger.debug("class name: {}, method name: {}, method descriptor: {}", className, methodName, methodDescriptor);
//
//        if (!className.startsWith("java.")) {
//            // resolution
//        }

        try {
            CtClass[] parameterTypes = Descriptor.getParameterTypes(methodDescriptor, method.getDeclaringClass().getClassPool());
            logger.debug("Number of arguments: {}", parameterTypes.length);
            for (CtClass paramType : parameterTypes) {
                logger.debug("Parameter Type: {}", paramType.getName());
            }

            int numberOfArguments = parameterTypes.length;
            List<Integer> derivativeArgs = new ArrayList<>();
            for (int i = 0; i < numberOfArguments; i++) {
                if (stack.pop().isDerivativeSet()) {
                    derivativeArgs.add(i);
                }
            }

            MethodAnalyser analyser = mainAnalyser.analyseMethod(className, methodName, derivativeArgs, methodDescriptor);

            var result = analyser.getAnalysisResult().getReturnState();
            if (!result.isUndefined()) {
                stack.push(result);
            }


        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }
//
//    private Entity analyseStaticMethod(int index) {
//        var classFile = method.getDeclaringClass().getClassFile();
//        var constPool = classFile.getConstPool();
//
//        int methodRefIndex = constPool.getMethodrefClass(index);
//        String className = constPool.getClassInfo(methodRefIndex);
//        String methodName = constPool.getMethodrefName(index);
//        String methodDescriptor = constPool.getMethodrefType(index);
//
//        logger.debug("class name: {}, method name: {}, method descriptor: {}", className, methodName, methodDescriptor);
//
//        MethodAnalyser analyser = mainAnalyser.analyseMethod(className, methodName, List.of(0));
//        return analyser.getAnalysisResult().getReturnState();
//    }

    private Entity createNonDerivative() {
        return new Entity(Entity.Type.NON_DERIVATIVE);
    }

    private Entity createDerivativeSuccessor(Entity oldValue, int line) {
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
        return mergeCurStates(predecessors);
    }

    private CurrentState mergeCurStates(List<CurrentState> currentStates) {
        if (currentStates.size() == 1) {
            return new CurrentState(currentStates.getFirst());
        }

        CurrentState curStateResult = CurrentState.getEmptyState(numOfVars, startingDerivatives);
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


    private int parseNextBytes(CodeIterator iterator, int index, int bytesNumber) {
        int result = 1;
        for (int i = 0; i < bytesNumber; i++) {
            int indexbyte = (iterator.byteAt(i + index + 1) & 0xff);
            result = result | (indexbyte << (bytesNumber - i - 1) * 8);
        }
        return result;
    }


}
