package entitites;

public class NonDerivative extends Entity {


    public NonDerivative() {

    }

    @Override
    public String info(int tabNumber) {
        return "\t".repeat(tabNumber) + "assigned to non derivative";
    }


    public static Entity createNonDerivative() {
        return new NonDerivative();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NonDerivative that = (NonDerivative) o;
        return super.equals(that);
    }
}
