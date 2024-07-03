package output;

import java.util.List;
import java.util.Objects;

public record Value(Type type, List<Value> predecessors, boolean isFunctionArgument) {

    public enum Type {
        DERIVATIVE,
        NON_DERIVATIVE,
        UNDEFINED
    }


    public boolean isDerivative() {
        return type.equals(Type.DERIVATIVE);
    }

    public boolean isNonDerivative() {
        return type.equals(Type.NON_DERIVATIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return isFunctionArgument == value.isFunctionArgument && type == value.type && Objects.equals(predecessors, value.predecessors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, predecessors, isFunctionArgument);
    }
}
