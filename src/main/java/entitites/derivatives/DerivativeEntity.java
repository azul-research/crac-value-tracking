package entitites.derivatives;

import entitites.Entity;

import java.util.Arrays;
import java.util.Objects;

public class DerivativeEntity extends Entity {

//    String functionName;
final Entity[] predecessors;

    public DerivativeEntity(int line, Entity... derivatives) {
        super(line);
        predecessors = derivatives;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DerivativeEntity that = (DerivativeEntity) o;
        return line == that.line && Arrays.equals(predecessors, that.predecessors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, Arrays.hashCode(predecessors));
    }

    @Override
    public String info(int tabNumber) {
        StringBuilder result = new StringBuilder();
        result.append("\t".repeat(tabNumber)).append("derivative from: ");
        for (var pred : predecessors) {
            result.append(pred.info(tabNumber + 1));
            result.append("\n");
        }
        return result.toString();
    }


    public boolean containsEntity(Entity entity) {
        for (var pred: predecessors) {
            if (pred.equals(entity)) {
                return true;
            }
            if (pred.isDerivative()) {
                if (((DerivativeEntity) pred).containsEntity(entity)) {
                    return true;
                }
            }
        }
        return false;
    }
}
