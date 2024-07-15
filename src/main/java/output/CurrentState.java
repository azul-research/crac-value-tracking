package output;

import entitites.*;
import entitites.derivatives.RootDerivative;

import java.util.*;

public class CurrentState {


    private final Entity[] variablesArray;
    private Entity returnState;


    public Entity[] getVariablesArray() {
        return variablesArray;
    }

    public Entity getReturnState() {
        return returnState;
    }


    public void updateReturnState(Entity entity) {
        this.returnState = entity;
    }
    public CurrentState(Entity[] variables) {
        this.variablesArray = variables;
        returnState = new UndefinedEntity();
    }

    public CurrentState(CurrentState state) {
        this.variablesArray = state.variablesArray.clone();
        this.returnState = state.returnState;
    }

    public static CurrentState getEmptyState(int numberOfVars, Map<Integer, String> derivatives) {
        Entity[] vars = new Entity[numberOfVars];
        for (int i = 0; i < numberOfVars; i++) {
            if (derivatives.containsKey(i)) {
                vars[i] = new DerivativeSet(new RootDerivative(derivatives.get(i)));
            } else {
                vars[i] = new UndefinedEntity();
            }
        }
        return new CurrentState(vars);
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
