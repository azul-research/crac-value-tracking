package entitites.derivatives;


import java.util.Arrays;
import java.util.Objects;

public class OperationDerivative extends Derivative {

    Derivative[] predecessors;
    String fileName;

    int line;
    public OperationDerivative(int line, String fileName, Derivative... predecessors) {
        super();
        this.line = line;
        this.fileName = fileName;
        this.predecessors = predecessors;
    }

    public String getFileName() {
        return fileName;
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
        OperationDerivative that = (OperationDerivative) o;
        return line == that.line && Objects.deepEquals(predecessors, that.predecessors) && Objects.equals(fileName, that.fileName);
    }


    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(predecessors), fileName, line);
    }

    public Derivative[] getPredecessors() {
        return predecessors;
    }

    public int getLine() {
        return line;
    }
}

