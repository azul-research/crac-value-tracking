package entitites;

public class NonDerivativeEntity extends Entity {


    int codeLine;

    public NonDerivativeEntity(int line) {
        super(line);
        codeLine = line;
    }

    @Override
    public String info(int tabNumber) {
        return "\t".repeat(tabNumber) + "assigned to non derivative on line " + codeLine;
    }


    public static Entity createNonDerivative(int lineNumber) {
        return new NonDerivativeEntity(lineNumber);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NonDerivativeEntity that = (NonDerivativeEntity) o;
        return super.equals(that);
    }
}
