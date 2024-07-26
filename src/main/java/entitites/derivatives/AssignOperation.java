package entitites.derivatives;

public class AssignOperation extends OperationDerivative {
    public AssignOperation(int line, String fileName, Derivative predecessors) {
        super(line, fileName, predecessors);
    }
}
