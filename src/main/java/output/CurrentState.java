package output;

import java.util.*;

public class CurrentState {


    List<Derivative> derivatives;


    Set<Integer> variables;

    Set<Value>[] variablesArray;


    Set<Integer> getVariables() {
        return variables;
    }

    Set<Value>[] getVariablesArray() {
        return variablesArray;
    }

    public CurrentState(Set<Value>[] variables) {
        this.variablesArray = variables;

    }

    public static CurrentState getEmptyState(int numberOfVars, Set<Integer> derivatives) {
        Set<Value>[] vars = new Set[numberOfVars];
        for (int i = 0; i < numberOfVars; i++) {
            if (derivatives.contains(i)) {
                vars[i] = new HashSet<>(Set.of(new Value(Value.Type.DERIVATIVE, List.of(), true)));
            }
            else {
                vars[i] = new HashSet<>(Set.of(new Value(Value.Type.UNDEFINED, List.of(), true)));
            }
        }
        return new CurrentState(vars);
    }


    public void mergeWith(CurrentState st1) {
        var newVariablesArray = st1.getVariablesArray();
        for (int i = 0 ; i < variablesArray.length; i++) {
            variablesArray[i].addAll(newVariablesArray[i]);
        }
    }

    public void updateVariable(Integer number, Set<Value> newValue) {
        variablesArray[number] = newValue;
    }

    public boolean isDerivative(Integer var) {
        return variablesArray[var].stream().anyMatch(a -> a.type().equals(Value.Type.DERIVATIVE));
    }

    public void removeVariable(Integer var) {
        variables.remove(var);
    }


    @Override
    public String toString() {
        return variables.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentState state = (CurrentState) o;
        return Objects.equals(variables, state.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variables);
    }
}
