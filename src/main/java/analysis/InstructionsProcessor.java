package analysis;

import entitites.Entity;
import entitites.derivatives.OperationDerivative;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.CurrentState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static entitites.Entity.createNonDerivative;

public class InstructionsProcessor {

    private static final Logger logger = LogManager.getLogger(InstructionsProcessor.class);


    MethodAnalyser analyser;
    CtBehavior method;


    public InstructionsProcessor(MethodAnalyser analyser, CtBehavior method) {
        this.analyser = analyser;
        this.method = method;
    }


    public void processBinOperation(int index, ArrayDeque<Entity> stack) {
        var first = stack.pop();
        var second = stack.pop();
        stack.push(analyser.createMergedEntity(first, second, analyser.getLineNumber(index)));
    }


    void processStoreToArray(int index, ArrayDeque<Entity> stack) {
        var value = stack.pop();
        var ind = stack.pop();
        var arrayRef = stack.pop();

        if (value.isDerivativeSet()) {
            if (arrayRef.isNonDerivative()) {
                arrayRef.setToDerivative();
                for (var derivative : value.getDerivativeSet()) {
                    arrayRef.addDerivative(new OperationDerivative(analyser.getLineNumber(index), derivative));
                }
            }
        }

    }


    void processIncrementLocal(int index, CurrentState state, CodeIterator iterator) {
        var varIndex = analyser.parseNextBytes(iterator, index, 1);
        if (state.isDerivative(varIndex)) {
            state.updateVariable(varIndex, analyser.createEntitySuccessor(state.getVarValue(index), analyser.getLineNumber(index)));
        } else {
            state.updateVariable(varIndex, createNonDerivative());
        }
    }


    void processCreateArray(int index, ArrayDeque<Entity> stack, CodeIterator iterator) {
        var count = stack.pop();
        var typeRef = analyser.parseNextBytes(iterator, index, 2);
        if (count.isDerivativeSet()) {
            stack.push(analyser.createEntitySuccessor(count, analyser.getLineNumber(index)));
        } else {
            stack.push(createNonDerivative());
        }
    }


    void processDataStore(int index, ArrayDeque<Entity> stack, CurrentState state, int varNumber) {
        var valueOnStack = stack.pop();
        if (valueOnStack.isDerivativeSet()) {
            state.updateVariable(varNumber, analyser.createEntitySuccessor(valueOnStack, analyser.getLineNumber(index)));
        } else {
            state.updateVariable(varNumber, valueOnStack);
        }
    }

    record StaticFieldInfo(String className, String name) {
    }


    void processPutStatic(int indexInConstPool, Entity entity, CurrentState state, int line) {
        StaticFieldInfo fieldInfo = getFieldNameAndClassName(indexInConstPool);

        analyser.getMainAnalyser().prepareClass(fieldInfo.className, state.getLoadedClasses(), state.getInitialisedClasses());

        state.getInitialisedClasses().get(fieldInfo.className).put(fieldInfo.name, analyser.createEntitySuccessor(entity, line));
    }

    Entity processGetStatic(int indexInConstPool, CurrentState state) {
        StaticFieldInfo fieldInfo = getFieldNameAndClassName(indexInConstPool);

        analyser.getMainAnalyser().prepareClass(fieldInfo.className, state.getLoadedClasses(), state.getInitialisedClasses());

        return state.getInitialisedClasses().get(fieldInfo.className).get(fieldInfo.name);

    }

    void processPutField(int index, ArrayDeque<Entity> stack) {
        var value = stack.pop();
        var objectRef = stack.pop();
        if (value.isDerivativeSet()) {
            if (!objectRef.isDerivativeSet()) {
                objectRef.setToDerivative();
                objectRef.setDerivativeSet(analyser.createEntitySuccessor(value, analyser.getLineNumber(index)).getDerivativeSet());
            } else {
                objectRef.setDerivativeSet(analyser.createMergedDerivativeSet(value, objectRef, analyser.getLineNumber(index)).getDerivativeSet());
            }

        }

    }


    private StaticFieldInfo getFieldNameAndClassName(int indexInConstPool) {
        ConstPool constPool = method.getDeclaringClass().getClassFile().getConstPool();

        String className = constPool.getFieldrefClassName(indexInConstPool);
        String fieldName = constPool.getFieldrefName(indexInConstPool);

        int nameAndTypeIndex = constPool.getFieldrefNameAndType(indexInConstPool);
        int fieldDescriptorIndex = constPool.getNameAndTypeDescriptor(nameAndTypeIndex);

        String fieldDescriptor = constPool.getUtf8Info(fieldDescriptorIndex);
        String fieldType = Descriptor.toClassName(fieldDescriptor);

        return new StaticFieldInfo(className, fieldName);
    }


    private List<Integer> getStaticMethodArguments(int numberOfArguments, ArrayDeque<Entity> stack) {
        List<Integer> derivativeArgs = new ArrayList<>();
        for (int i = numberOfArguments - 1; i >= 0; i--) {
            if (stack.pop().isDerivativeSet()) {
                derivativeArgs.add(i);
            }
        }
        return derivativeArgs;
    }


    private List<Integer> getNonstaticMethodArguments(int numberOfArguments, ArrayDeque<Entity> stack) {
        List<Integer> derivativeArgs = new ArrayList<>();
        for (int i = numberOfArguments; i > 0; i--) {
            if (stack.pop().isDerivativeSet()) {
                derivativeArgs.add(i);
            }
        }

        return derivativeArgs;
    }


    void processInvokeMethod(int index, ArrayDeque<Entity> stack, CurrentState state, boolean isStatic) {
        try {
            var constPool = method.getDeclaringClass().getClassFile().getConstPool();
            int methodRefIndex = constPool.getMethodrefClass(index);
            String className = constPool.getClassInfo(methodRefIndex);
            String methodName = constPool.getMethodrefName(index);
            String methodDescriptor = constPool.getMethodrefType(index);
            logger.debug("class name: {}, method name: {}, method descriptor: {}", className, methodName, methodDescriptor);

            int numberOfArguments = Descriptor.getParameterTypes(methodDescriptor, method.getDeclaringClass().getClassPool()).length;

            List<Integer> derivativeArgs;

            Optional<Entity> objectRef = Optional.empty();

            if (isStatic) {
                derivativeArgs = getStaticMethodArguments(numberOfArguments, stack);
            } else {
                derivativeArgs = getNonstaticMethodArguments(numberOfArguments, stack);
                objectRef = Optional.of(stack.pop());
                if (objectRef.get().isDerivativeSet()) {
                    derivativeArgs.add(0);
                }

            }

            CurrentState resultState = analyser.mainAnalyser.analyseMethod(className, methodName, derivativeArgs, methodDescriptor, state.getLoadedClasses(), state.getInitialisedClasses(), objectRef);

            var result = resultState.getReturnState();
            result.ifPresent(stack::push);


        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
