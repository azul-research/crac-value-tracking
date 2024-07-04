package entitites;

public class StraightDerivative extends DerivativeEntity {
    public StraightDerivative(int line, DerivativeEntity derivatives) {
        super(line, derivatives);
    }

    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "assigned to derivative on line " + this.line + ":");
        this.predecessors[0].print(tabNumber + 1);
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
