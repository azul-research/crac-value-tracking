package analysis;

import entitites.*;
import entitites.derivatives.OperationDerivative;
import javassist.CtBehavior;
import javassist.bytecode.*;
import javassist.bytecode.analysis.ControlFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.*;

import java.util.*;
import java.lang.String;
import java.util.List;

import static analysis.InstructionsMatcher.*;
import static analysis.InstructionsProcessor.parseNextBytes;
import static entitites.Entity.createNonDerivative;
import static output.CurrentState.getEmptyState;

public class MethodAnalyser {
    private static final Logger logger = LogManager.getLogger(MethodAnalyser.class);
    Map<Integer, ControlFlow.Block> methodCFG;

    ControlFlow.Block[] blocks;

    Map<Integer, CurrentState> currentBlocksStates;
    //    CtMethod method;
    CodeAttribute codeAttribute;

    InstructionsProcessor processor;
    int startBlock;
    Set<Integer> endBlocks;
    CurrentState finalState;

    int numOfVars;
    String fileName;
    Optional<Entity> thisObj;

    Analyser mainAnalyser;
    CtBehavior method;
    ArrayDeque<MyMethod> stacktrace;

    JVMState startingJvmState;
    Map<Integer, Entity> startingDerivatives;

    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};

    public MethodAnalyser(ControlFlow.Block[] cfgBlocks, CtBehavior method, Map<Integer, Entity> derivativeVars, Analyser mainAnalyser, Optional<Entity> thisObject, JVMState jvmState, ArrayDeque<MyMethod> stacktrace) {
        this.stacktrace = stacktrace;

        this.thisObj = thisObject;
        this.mainAnalyser = mainAnalyser;
        this.codeAttribute = method.getMethodInfo().getCodeAttribute();
        this.method = method;
        setFileName(method);

        setStartingDerivatives(derivativeVars);
        setMethodCFG(cfgBlocks);

        this.blocks = cfgBlocks;
        this.startBlock = Collections.min(this.methodCFG.keySet());
        setEndBlocks();

//        logger.debug("Start block: {}, end block: {}", startBlock, endBlock);

        this.numOfVars = codeAttribute.getMaxLocals();
        this.startingJvmState = jvmState;
        setCurrentBlocksStates(numOfVars, this.startingDerivatives, jvmState);

        this.processor = new InstructionsProcessor(this, method);

//        logger.debug("Number of local variables: {}", numOfVars);

    }

    private void setEndBlocks() {
        this.endBlocks = new HashSet<>();
        for (var block : blocks) {
            if (block.exits() == 0) {
                this.endBlocks.add(block.position());
            }
        }
    }

    private CurrentState getFinalState() {
        ArrayList<CurrentState> currentStates = new ArrayList<>();

        for (var blockIndex : endBlocks) {
            currentStates.add(currentBlocksStates.get(blockIndex));
        }

        finalState = mergeCurrentStates(currentStates);
        return finalState;
    }


    public CurrentState getAnalysisResult() {
        return getFinalState();
    }

    String getVarName(int i) {
        var attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        var attribute2 = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        System.out.println(attribute2.tableLength());
        return attribute2.variableName(i);
    }

    int getLocalsNumber() {
        var attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        return attribute.tableLength();

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

    private void setCurrentBlocksStates(int numOfVars, Map<Integer, Entity> derivativeVars, JVMState jvmState) {
        currentBlocksStates = new HashMap<>();
        for (var index : methodCFG.keySet()) {
            currentBlocksStates.put(index, getEmptyState(numOfVars, derivativeVars, jvmState));
        }
    }

    private void setStartingDerivatives(Map<Integer, Entity> derivativeVars) {
        this.startingDerivatives = new HashMap<>(derivativeVars);

    }

    public void analyse() {
//        logger.debug("Control flow graph:");
//        for (var bl : methodCFG.keySet().stream().sorted().toArray(Integer[]::new)) {
//            logger.debug(methodCFG.get(bl));
//        }
        analyseAllBlocks();

        CurrentState previousState;
        do {
            previousState = getFinalState();
            analyseAllBlocks();
        } while (!previousState.equals(getFinalState()));

//        logFinalState(finalState);

    }

    private void analyseAllBlocks() {
        logger.debug("Start analysis, method: {}, class name: {}", method.getName(), method.getDeclaringClass().getName());
        for (var block : blocks) {
            analyseBasicBlock(block.position());
        }
        logger.debug("End analysis, method: {}, class name: {}", method.getName(), method.getDeclaringClass().getName());
    }


    private void logFinalState(CurrentState finalState) {
        logger.info("Variables at the end of the method:");
        for (int i = 0; i < numOfVars; i++) {
            logger.info("{} - {}", getVarName(i), finalState.getVariableValue(i).info(0));
        }

        logger.info("Static fields at the end of the method:");
        for (var cl : finalState.getInitialisedClasses().entrySet()) {
            logger.info("Fields of class {}", cl.getKey());
            for (var field : cl.getValue().getStaticFields().entrySet()) {
                logger.info("{} - {}", field.getKey(), field.getValue().info(0));

            }
        }
    }

    void analyseBasicBlock(int blockIndex) {
//        logger.debug("Analysing block: {}", blockIndex);
        CurrentState startingState;
        if (blockIndex == startBlock) {
            startingState = getEmptyState(numOfVars, startingDerivatives, startingJvmState);
        } else {
            startingState = getBlockStartingState(blockIndex);
        }
        analyseCode(startingState, blockIndex);

        currentBlocksStates.put(blockIndex, startingState);

    }

    void analyseCode(CurrentState state, int blockIndex) {
        int blockEnd = methodCFG.get(blockIndex).length() + blockIndex;
        var iterator = new MyCodeIterator(codeAttribute);
        byte[] code = codeAttribute.getCode();
        int index = blockIndex;

        ArrayDeque<Entity> stack = state.getStack();

        while (index < blockEnd) {
            int opcode = iterator.byteAt(index);
            String name = Mnemonic.OPCODE[opcode];
            logger.debug("{}:{} instruction:{} {}", fileName, getLineNumber(index), index, name);

            if (matchConstLoad(name) || matchConstLoadFromPool(name) || matchByteLoad(name)) {
                stack.push(createNonDerivative());

            } else if (matchStoreData(name)) {
                var varNumber = getNumberInOpcode(name);
                processor.processDataStore(index, stack, state, varNumber);

            } else if (matchStoreToVariable(name)) {
                int varNumber = parseNextBytes(iterator, index, 1);
                processor.processDataStore(index, stack, state, varNumber);

            } else if (matchLoadVariable(name)) {
                var varNumber = getNumberInOpcode(name);
                stack.push(state.getVariableValue(varNumber));
            } else if (matchLoadVariableWithoutIndex(name)) {
                var variableNumber = parseNextBytes(iterator, index, 1);
                stack.push(state.getVariableValue(variableNumber));
            } else if (matchLoadArrayElem(name)) {
                processor.processLoadArrayElement(stack);
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
            } else if (matchInvokeInterface(name)) {
                var methodIndex = parseNextBytes(iterator, index, 2);
//                processor.processInvokeInterface(methodIndex, stack);
            } else if (matchInvokeStatic(name) | matchInvokeDynamic(name)) {
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
                processor.processNew(iterator, index, stack);
            } else if (matchDuplicateValue(name)) {
                stack.push(stack.getFirst());
            } else if (matchPop(name) | matchMonitor(name)) {
                stack.pop();
            } else if (matchPop2(name)) {
                stack.pop();
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (matchIfWith2Arguments(name)) {
                stack.pop();
                stack.pop();
            } else if (matchIfWith1Argument(name)) {
                stack.pop();
            } else if (matchTableSwitch(name)) {
                stack.pop();
            } else if (matchThrowError(name)) {
                var objectRef = stack.pop();
                stack.clear();
                stack.push(objectRef);
            } else if (matchSwap(name)) {
                processor.processSwap(stack);
            } else if (!matchGoto(name) && !matchConvertValue(name)) {
                logger.warn("UNKNOWN OPCODE: {}", name);
            }

            index = iterator.myNextOpcode(code, index);

        }
    }


    Entity createEntitiesUnion(Entity first, Entity second, int codeLine) {
        if (first.compareType(second) > 0) {
            return first;
        } else if (first.compareType(second) < 0) {
            return second;
        } else if (first.compareType(second) == 0 && first.isDerivativeSet()) {
            return createDerivativeSetsUnion(first, second, codeLine);
        } else {
            return first;
        }
    }

    Entity createDerivativeSetsUnion(Entity first, Entity second, int codeLine) {
        assert first.isDerivativeSet();
        assert second.isDerivativeSet();

        Entity result = new Entity(Entity.Type.DERIVATIVE_SET);

        for (var pred1 : first.getDerivativeSet()) {
            for (var pred2 : second.getDerivativeSet()) {
                result.addDerivative(new OperationDerivative(codeLine, fileName, pred1, pred2));
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
            newValue.addDerivative(new OperationDerivative(line, fileName, pred));
        }
        return newValue;
    }

    private CurrentState getBlockStartingState(int blockIndex) {
        var block = methodCFG.get(blockIndex);
        int num = block.incomings();
        List<CurrentState> predecessors = new ArrayList<>();
        int incomingBlockIndex;
        for (int i = 0; i < num; i++) {
            incomingBlockIndex = block.incoming(i).position();
            predecessors.add(currentBlocksStates.get(incomingBlockIndex));
        }
        return mergeCurrentStates(predecessors);
    }

    private CurrentState mergeCurrentStates(List<CurrentState> currentStates) {
        if (currentStates.size() == 1) {
            return new CurrentState(currentStates.getFirst());
        }

        CurrentState newCurrentState = getEmptyState(numOfVars, startingDerivatives, startingJvmState);

        if (currentStates.isEmpty()) {
            return newCurrentState;
        }

        // merge stacks
        mergeStacks(currentStates, newCurrentState);

        // merge local variables
        mergeVariables(currentStates, newCurrentState);

        // merge jvm states
        mergeJvmStates(currentStates, newCurrentState);

        // merge return states
        mergeReturnStates(currentStates, newCurrentState);

        return newCurrentState;
    }


    private void mergeReturnStates(List<CurrentState> currentStates, CurrentState newCurrentState) {
        Optional<Entity> newReturnState = Optional.empty();

        for (var curState : currentStates) {
            newReturnState = mergeOptionals(newReturnState, curState.getReturnState());
        }

        newReturnState.ifPresent(newCurrentState::updateReturnState);
    }

    Optional<Entity> mergeOptionals(Optional<Entity> optional1, Optional<Entity> optional2) {
        if (optional1.isPresent() && optional2.isPresent()) {
            return Optional.of(mergeTwoEntities(optional1.get(), optional2.get()));
        }
        else if (optional1.isPresent()) {
            return optional1;
        }
        else {
            return optional2;
        }
    }


    private void mergeJvmStates(List<CurrentState> currentStates, CurrentState newCurrentState) {
        // merge loaded classes
        mergeLoadedClasses(currentStates, newCurrentState);

        // merge initialized classes and static fields
        mergeInitializedClasses(currentStates, newCurrentState);
    }

    private void mergeLoadedClasses(List<CurrentState> currentStates, CurrentState newCurrentState) {
        for (var state : currentStates) {
            newCurrentState.getLoadedClasses().addAll(state.getLoadedClasses());
        }
    }

    private void mergeInitializedClasses(List<CurrentState> currentStates, CurrentState newCurrentState) {
        Map<MyClass, ClassStaticFields> newInitialisedClasses = new HashMap<>();
        for (var state : currentStates) {
            for (var myClass : state.getInitialisedClasses().entrySet()) {
                MyClass myClassName = myClass.getKey();
                if (!newInitialisedClasses.containsKey(myClassName)) {
                    newInitialisedClasses.put(myClassName, myClass.getValue());
                } else {
                    newInitialisedClasses.put(myClassName, mergeStaticFields(newInitialisedClasses.get(myClassName), myClass.getValue()));
                }
            }

        }

        newCurrentState.updateInitialisedClasses(newInitialisedClasses);
    }


    private ClassStaticFields mergeStaticFields(ClassStaticFields staticFields1, ClassStaticFields staticFields2) {
        Map<String, Entity> result = new HashMap<>();
        for (var fieldName : staticFields1.getStaticFields().keySet()) {
            result.put(fieldName, mergeTwoEntities(staticFields1.getField(fieldName), staticFields2.getField(fieldName)));
        }

        return new ClassStaticFields(staticFields1.getMyClass(), result);
    }

    private void mergeStacks(List<CurrentState> currentStates, CurrentState newCurrentState) {
        int stackSize = currentStates.stream().map(a -> a.getStack().size()).max(Integer::compare).get();
        if (stackSize == 0) {
            return;
        }
        Entity[] newStack = new Entity[stackSize];

        for (int i = 0; i < stackSize; i++) {
            newStack[i] = new Entity(Entity.Type.UNDEFINED);
            for (var state : currentStates) {
                if (state.getStack().size() <= i) {
                    continue;
                }
                Entity entity = state.getStack().toArray(Entity[]::new)[i];
                newStack[i] = mergeTwoEntities(newStack[i], entity);
            }
        }
        newCurrentState.updateStack(new ArrayDeque<>(Arrays.asList(newStack)));
    }

    void mergeVariables(List<CurrentState> currentStates, CurrentState newCurrentState) {
        for (int i = 0; i < numOfVars; i++) {
            newCurrentState.updateVariable(i, mergeVariableEntities(currentStates, i));
        }
    }

    Entity mergeVariableEntities(List<CurrentState> currentStates, int varNumber) {
        Entity newEntity = new Entity(Entity.Type.UNDEFINED);

        for (var state : currentStates) {
            Entity entity = state.getVariableValue(varNumber);
            newEntity = mergeTwoEntities(entity, newEntity);
        }
        return newEntity;
    }


    Entity mergeTwoEntities(Entity first, Entity second) {
        if (first.compareType(second) > 0) {
            first.addClassNames(second.getClassNameSet());
            return first;
        } else if (first.compareType(second) < 0) {
            second.addClassNames(first.getClassNameSet());
            return second;
        } else if (first.compareType(second) == 0 && first.isDerivativeSet()) {
            return new Entity(Entity.Type.DERIVATIVE_SET, first, second);
        } else {
            first.addClassNames(second.getClassNameSet());
            return first;
        }

    }

}
