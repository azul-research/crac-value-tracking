package entitites.derivatives;


import java.util.Arrays;
import java.util.Objects;

public class OpDerivative extends Derivative {

    Derivative[] predecessors;

    int line;
    public OpDerivative(int line, Derivative... predecessors) {
        super();
        this.line = line;
        this.predecessors = predecessors;
    }


    @Override
    public String info(int tabNumber) {
        StringBuilder result = new StringBuilder();
        result.append("\t".repeat(tabNumber));
        result.append("derivative on line ").append(line).append(" from: \n");
        for (var pred : predecessors) {
            result.append(pred.info(tabNumber + 1));
        }

        return result.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OpDerivative that = (OpDerivative) o;
        return line == that.line && Objects.deepEquals(predecessors, that.predecessors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(predecessors), line);
    }
}

