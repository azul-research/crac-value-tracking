package output;

import java.util.ArrayList;
import java.util.List;

public class Derivative {

    private int number;
//    String functionName;
    private List<Derivative> predecessors = new ArrayList<>();
    private List<Derivative> successors = new ArrayList<>();

    public Derivative(int number, boolean isFunctionArgument) {
        this.number = number;
    }

    public void addPredecessor(Derivative predecessor) {
        predecessors.add(predecessor);
    }

    public void addSuccessors(Derivative successor) {
        successors.add(successor);
    }
}
