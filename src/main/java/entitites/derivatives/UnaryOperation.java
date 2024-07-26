package entitites.derivatives;

public class UnaryOperation extends OperationDerivative {
    public UnaryOperation(int line,String fileName,  Derivative predecessor) {
        super(line, fileName, predecessor);
    }
}
