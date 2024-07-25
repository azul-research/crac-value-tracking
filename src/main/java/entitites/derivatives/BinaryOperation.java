package entitites.derivatives;

public class BinaryOperation extends OperationDerivative {
    public BinaryOperation(int line, Derivative leftPredecessor, Derivative rightPredecessor) {
        super(line, leftPredecessor, rightPredecessor);
    }
}
