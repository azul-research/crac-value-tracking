package entitites;

public class NonDerivativeEntity extends Entity {

    public NonDerivativeEntity(int line) {
        super(line);
    }

    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "assigned to non derivative on line " + line);
    }


    public static Entity createNonDerivative(int lineNumber) {
        return new NonDerivativeEntity(lineNumber);
    }


}
