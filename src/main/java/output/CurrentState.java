package output;

import entitites.*;
import entitites.derivatives.RootDerivative;

import java.util.*;


public class CurrentState {

    private final Set<String> loadedClasses;
    private final Map<String, Map<String, Entity>> initialisedClasses;

    private final Entity[] variablesArray;
    private Entity returnState;
    private ArrayDeque<Entity> stack;

    public Set<String> getLoadedClasses() {
        return loadedClasses;
    }

    public Map<String, Map<String, Entity>> getInitialisedClasses() {
        return initialisedClasses;
    }

    public ArrayDeque<Entity> getStack() {
        return stack;
    }

    public void updateStack(ArrayDeque<Entity> stack) {
        this.stack = stack;
    }

    public Entity[] getVariablesArray() {
        return variablesArray;
    }

    public Entity getReturnState() {
        return returnState;
    }


    public void updateReturnState(Entity entity) {
        this.returnState = entity;
    }
    public CurrentState(Entity[] variables, ArrayDeque<Entity> stack, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        this.variablesArray = variables;
        returnState = new Entity(Entity.Type.UNDEFINED);
        this.stack = stack;
        this.loadedClasses = loadedClasses;
        this.initialisedClasses = initialisedClasses;
    }

    public CurrentState(CurrentState state) {
        this.variablesArray = state.variablesArray.clone();
        this.returnState = state.returnState;
        this.stack = state.stack;
        this.loadedClasses = state.loadedClasses;
        this.initialisedClasses = state.initialisedClasses;
    }

    public static CurrentState getEmptyState(int numberOfVars, Map<Integer, String> derivatives, Set<String> loadedClasses, Map<String, Map<String, Entity>> initialisedClasses) {
        Entity[] vars = new Entity[numberOfVars];
        for (int i = 0; i < numberOfVars; i++) {
            if (derivatives.containsKey(i)) {
                vars[i] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative(derivatives.get(i)));
            } else {
                vars[i] = new Entity(Entity.Type.UNDEFINED);
            }
        }
        return new CurrentState(vars, new ArrayDeque<>(), loadedClasses, initialisedClasses);
    }


    public void updateVariable(Integer number, Entity newValue) {
        variablesArray[number] = newValue;
    }

    public boolean isDerivative(Integer var) {
        return variablesArray[var].isDerivativeSet();
    }

    public Entity getVarValue(int var) {
        return variablesArray[var];
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
