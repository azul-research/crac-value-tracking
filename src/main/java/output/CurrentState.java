package output;

import entitites.*;
import entitites.derivatives.RootDerivative;

import java.util.*;

public class CurrentState {

    private final Entity[] variablesArray;
    private ArrayDeque<Entity> stack;
    private Optional<Entity> returnState;

    private final JVMState jvmState;

    public CurrentState(Entity[] variables, ArrayDeque<Entity> stack, Optional<Entity> returnState, JVMState jvmState) {
        this.variablesArray = variables.clone();
        this.returnState = returnState;
        this.stack = new ArrayDeque<>(stack);
        this.jvmState = new JVMState(jvmState);
    }

    public CurrentState(CurrentState state) {
        this.variablesArray = state.variablesArray.clone();
        this.returnState = state.returnState;
        this.stack = new ArrayDeque<>(state.stack);
        this.jvmState = new JVMState(state.jvmState);
    }

    public Set<MyClass> getLoadedClasses() {
        return jvmState.getLoadedClasses();
    }

    public Map<MyClass, ClassStaticFields> getInitialisedClasses() {
        return jvmState.getInitializedClasses();
    }

    public ArrayDeque<Entity> getStack() {
        return stack;
    }

    public Entity[] getVariablesArray() {
        return variablesArray;
    }

    public Optional<Entity> getReturnState() {
        return returnState;
    }

    public JVMState getJvmState() {
        return jvmState;
    }

    public Entity getVariableValue(int var) {
        return variablesArray[var];
    }

    public void setEmptyReturn() {
        returnState = Optional.empty();
    }

    public void updateStack(ArrayDeque<Entity> stack) {
        this.stack = new ArrayDeque<>(stack);
    }

    public void updateReturnState(Entity entity) {
        this.returnState = Optional.of(entity);
    }

    public void updateVariable(Integer number, Entity newValue) {
        variablesArray[number] = newValue;
    }

    public void updateInitialisedClasses(Map<MyClass, ClassStaticFields> newInitialisedClasses) {
        jvmState.setInitializedClasses(newInitialisedClasses);
    }

    public boolean isVariableDerivative(Integer var) {
        return variablesArray[var].isDerivativeSet();
    }

    public static CurrentState getEmptyState(int numberOfVars, Map<Integer, String> derivatives, JVMState jvmState) {
        Entity[] localVariables = new Entity[numberOfVars];
        for (int i = 0; i < numberOfVars; i++) {
            if (derivatives.containsKey(i)) {
                localVariables[i] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative(derivatives.get(i)));
            } else {
                localVariables[i] = new Entity(Entity.Type.UNDEFINED);
            }
        }
        return new CurrentState(localVariables, new ArrayDeque<>(), Optional.empty(), jvmState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentState state = (CurrentState) o;
        return Objects.deepEquals(variablesArray, state.variablesArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(variablesArray);
    }

}
