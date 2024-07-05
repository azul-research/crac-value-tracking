package output;

import entitites.*;

import java.util.*;

public class CurrentState {


    Entity[] variablesArray;


    public Entity[] getVariablesArray() {
        return variablesArray;
    }

    public CurrentState(Entity[] variables) {
        this.variablesArray = variables;

    }

    public static CurrentState getEmptyState(int numberOfVars, Map<Integer, String> derivatives) {
        Entity[] vars = new Entity[numberOfVars];

        for (int i = 0; i < numberOfVars; i++) {
            if (derivatives.containsKey(i)) {
                vars[i] = new RootDerivative(derivatives.get(i));
            } else {
                vars[i] = new UndefinedEntity();
            }
        }
        return new CurrentState(vars);
    }


//    public void mergeWith(CurrentState st1) {
//        var newVariablesArray = st1.getVariablesArray();
//        Entity derivativeA;
//        Entity derivativeB;
//        Entity newEntity;
//        for (int i = 0; i < variablesArray.length; i++) {
//            derivativeA = variablesArray[i];
//            derivativeB = newVariablesArray[i];
//
//            if (!derivativeA.equals(derivativeB)) {
//                if (derivativeA.isUndefined()) {
//                    newEntity = derivativeB;
//                } else if (derivativeB.isUndefined()) {
//                    newEntity = derivativeA;
//                } else if (derivativeA.isNonDerivative() && derivativeB.isNonDerivative()) {
//                    newEntity = new NonDerivativeEntity(-1);
//                } else {
//                    newEntity = new PhiDerivative(0, derivativeA, derivativeB);
//                }
//            } else {
//                newEntity = variablesArray[i];
//            }
//            variablesArray[i] = newEntity;
//
//        }
//    }

    public void updateVariable(Integer number, Entity newValue) {
        variablesArray[number] = newValue;
    }

    public boolean isDerivative(Integer var) {
        return variablesArray[var].isDerivative();
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
