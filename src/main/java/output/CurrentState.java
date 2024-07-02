package output;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CurrentState {


    Set<Integer> variables;


    Set<Integer> getVariables() {
        return variables;
    }

    CurrentState(Set<Integer> variables) {
        this.variables = variables;
    }

    public static CurrentState getEmptyState() {
        return new CurrentState(new HashSet<>());
    }


    public void mergeWith(CurrentState st1) {
        variables.addAll(st1.getVariables());
    }


    public void addVariable(Integer var) {
        variables.add(var);
    }

    public boolean containsVariable(Integer var) {
        return variables.contains(var);
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
