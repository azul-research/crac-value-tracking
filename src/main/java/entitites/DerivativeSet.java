package entitites;

import entitites.derivatives.Derivative;

import java.util.HashSet;
import java.util.Set;

public class DerivativeSet extends Entity {
    Set<Derivative> predecessors;

    public DerivativeSet() {
        super();
        predecessors = new HashSet<>();
    }

    public void addPredecessors(Derivative predecessor) {
        predecessors.add(predecessor);
    }

    @Override
    public String info(int tabNumber) {
        if (predecessors.size() == 1) {
            return predecessors.iterator().next().info(tabNumber);
        }

        StringBuilder result = new StringBuilder();
        result.append("/t".repeat(tabNumber));
        result.append("Derivative from any of this:\n");

        for (Derivative predecessor : predecessors) {
            result.append(predecessor.info(tabNumber + 1));
            result.append("\n");
        }
        return result.toString();
    }

    public DerivativeSet(Derivative predecessor) {
        predecessors = new HashSet<>(Set.of(predecessor));
    }

    public Set<Derivative> getPredecessors() {
        return predecessors;
    }

    public void mergeWithDerivativeSet(DerivativeSet derivativeSet) {
        predecessors.addAll(derivativeSet.predecessors);
    }

}
