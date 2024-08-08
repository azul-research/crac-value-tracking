package analysis;

import entitites.Entity;
import entitites.derivatives.OperationDerivative;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import output.CurrentState;
import output.MyClass;
import output.MyMethod;

import java.util.*;

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
        stack.push(analyser.createEntitiesUnion(first, second, analyser.getLineNumber(index)));
    }


    void processStoreToArray(int index, ArrayDeque<Entity> stack) {
        var value = stack.pop();
        var ind = stack.pop();
        var arrayRef = stack.pop();

        if (value.isDerivativeSet()) {
            if (arrayRef.isNonDerivative()) {
                arrayRef.setToDerivative();
                for (var derivative : value.getDerivativeSet()) {
                    arrayRef.addDerivative(new OperationDerivative(analyser.getLineNumber(index), analyser.fileName, derivative));
                }
            }
        }

    }


    void processIncrementLocal(int index, CurrentState state, CodeIterator iterator) {
        var varIndex = parseNextBytes(iterator, index, 1);
        if (state.isVariableDerivative(varIndex)) {
            state.updateVariable(varIndex, analyser.createEntitySuccessor(state.getVariableValue(index), analyser.getLineNumber(index)));
        } else {
            state.updateVariable(varIndex, createNonDerivative());
        }
    }

    void processLoadArrayElement(ArrayDeque<Entity> stack) {
        var arrayIndex = stack.pop();
        var valueOnStack = stack.pop();
        stack.push(valueOnStack);
    }

    void processCreateArray(int index, ArrayDeque<Entity> stack, CodeIterator iterator) {
        var count = stack.pop();
        var typeRef = parseNextBytes(iterator, index, 2);
        if (count.isDerivativeSet()) {
            stack.push(analyser.createEntitySuccessor(count, analyser.getLineNumber(index)));
        } else {
            stack.push(createNonDerivative());
        }
    }

    void processSwap(ArrayDeque<Entity> stack) {
        var first = stack.pop();
        var second = stack.pop();
        stack.push(first);
        stack.push(second);
    }


    void processDataStore(int index, ArrayDeque<Entity> stack, CurrentState state, int variableNumber) {
        var valueOnStack = stack.pop();
        if (valueOnStack.isDerivativeSet()) {
            state.updateVariable(variableNumber, analyser.createEntitySuccessor(valueOnStack, analyser.getLineNumber(index)));
        } else {
            state.updateVariable(variableNumber, valueOnStack);
        }
    }

    public void processInvokeInterface(int index, ArrayDeque<Entity> stack) {
        try {

            var constPool = method.getDeclaringClass().getClassFile().getConstPool();
            int methodRefIndex = constPool.getMethodrefClass(index);
            String className = constPool.getClassInfo(methodRefIndex);
            String methodName = constPool.getMethodrefName(index);
            String methodDescriptor = constPool.getMethodrefType(index);
            logger.debug("class name: {}, method name: {}, method descriptor: {}", className, methodName, methodDescriptor);

            int numberOfArguments = Descriptor.getParameterTypes(methodDescriptor, method.getDeclaringClass().getClassPool()).length;

            Map<Integer, Entity> derivativeArgs;

            derivativeArgs = getNonstaticMethodArguments(numberOfArguments, stack);
           var  objectRef = Optional.of(stack.pop());
//            System.out.println(objectRef.get().getClassNameSet());

        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void processNew(CodeIterator iterator, int index, ArrayDeque<Entity> stack) {
        int typeIndex = parseNextBytes(iterator, index, 2);
        MethodInfo methodInfo = method.getMethodInfo();
        ConstPool constPool = methodInfo.getConstPool();
        String className = constPool.getClassInfo(typeIndex);
        System.out.println(className);
        stack.push(createNonDerivative());
    }

    record StaticFieldInfo(String className, String name) {
    }


    void processPutStatic(int indexInConstPool, Entity entity, CurrentState state, int line) {
        StaticFieldInfo fieldInfo = getFieldNameAndClassName(indexInConstPool);

        analyser.getMainAnalyser().prepareClass(fieldInfo.className, state.getJvmState(), analyser.stacktrace);

        MyClass myClass = new MyClass(fieldInfo.className);
        state.getJvmState().addClassStaticField(myClass, fieldInfo.name, analyser.createEntitySuccessor(entity, line));
    }

    Entity processGetStatic(int indexInConstPool, CurrentState state) {
        StaticFieldInfo fieldInfo = getFieldNameAndClassName(indexInConstPool);

        analyser.getMainAnalyser().prepareClass(fieldInfo.className, state.getJvmState(),  analyser.stacktrace);

        return state.getJvmState().getStaticField(new MyClass(fieldInfo.className), fieldInfo.name);

    }

    void processPutField(int index, ArrayDeque<Entity> stack) {
        var value = stack.pop();
        var objectRef = stack.pop();
        if (value.isDerivativeSet()) {
            if (!objectRef.isDerivativeSet()) {
                objectRef.setToDerivative();
                objectRef.setDerivativeSet(analyser.createEntitySuccessor(value, analyser.getLineNumber(index)).getDerivativeSet());
            } else {
                objectRef.setDerivativeSet(analyser.createDerivativeSetsUnion(value, objectRef, analyser.getLineNumber(index)).getDerivativeSet());
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


    private Map<Integer, Entity> getStaticMethodArguments(int numberOfArguments, ArrayDeque<Entity> stack) {
        Map<Integer, Entity> derivativeArgs = new HashMap<>();
        for (int i = numberOfArguments - 1; i >= 0; i--) {
            derivativeArgs.put(i, stack.pop());
        }
        return derivativeArgs;
    }


    private Map<Integer, Entity> getNonstaticMethodArguments(int numberOfArguments, ArrayDeque<Entity> stack) {
        Map<Integer, Entity> derivativeArgs = new HashMap<>();
        for (int i = numberOfArguments; i > 0; i--) {
            derivativeArgs.put(i, stack.pop());
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
//            logger.debug("Class name: {}, method name: {}, method descriptor: {}", className, methodName, methodDescriptor);

            int numberOfArguments = Descriptor.getParameterTypes(methodDescriptor, method.getDeclaringClass().getClassPool()).length;

            Map<Integer, Entity> derivativeArgs;

            Optional<Entity> objectRef = Optional.empty();

            if (isStatic) {
                derivativeArgs = getStaticMethodArguments(numberOfArguments, stack);
            } else {
                derivativeArgs = getNonstaticMethodArguments(numberOfArguments, stack);
                objectRef = Optional.of(stack.pop());
                derivativeArgs.put(0, objectRef.get());
            }


            CurrentState resultState = analyser.mainAnalyser.analyseMethod(className, methodName, derivativeArgs, methodDescriptor, objectRef, state.getJvmState(), analyser.stacktrace);



            var result = resultState.getReturnState();
            result.ifPresent(stack::push);


        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    static int parseNextBytes(CodeIterator iterator, int index, int bytesNumber) {
        int result = 0;
        for (int i = 0; i < bytesNumber; i++) {
            int indexbyte = (iterator.byteAt(i + index + 1) & 0xff);
            result = result | (indexbyte << (bytesNumber - i - 1) * 8);
        }
        return result;
    }
}
