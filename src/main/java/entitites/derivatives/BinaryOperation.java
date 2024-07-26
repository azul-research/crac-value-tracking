package entitites.derivatives;

public class BinaryOperation extends OperationDerivative {
    public BinaryOperation(int line, String fileName, Derivative leftPredecessor, Derivative rightPredecessor) {
        super(line, fileName, leftPredecessor, rightPredecessor);
    }
}
