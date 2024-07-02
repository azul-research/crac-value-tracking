package output;

import java.util.List;

public record Value(Type type, List<Value> predecessors, boolean isFunctionArgument) {

    public enum Type {
        DERIVATIVE,
        NON_DERIVATIVE,
        UNDEFINED
    }



}
