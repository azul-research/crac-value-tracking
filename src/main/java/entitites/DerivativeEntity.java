package entitites;

import java.util.Arrays;
import java.util.Objects;

public class DerivativeEntity extends Entity {

    int line;

//    String functionName;
final DerivativeEntity[] predecessors;

    public DerivativeEntity(int line, DerivativeEntity... derivatives) {
        super(line);
        predecessors = derivatives;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DerivativeEntity that = (DerivativeEntity) o;
        return line == that.line && Objects.deepEquals(predecessors, that.predecessors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, Arrays.hashCode(predecessors));
    }

    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "derivative from: ");
        for (var pred : predecessors) {
            pred.print(tabNumber + 1);
        }
    }
}
