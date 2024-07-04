package entitites;

public class PhiDerivative extends DerivativeEntity {
    public PhiDerivative(int line, Entity... derivatives) {
        super(line, derivatives);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PhiDerivative that = (PhiDerivative) o;
        return super.equals(that);
    }



    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "derivative from any of this: ");
        for (var pred : predecessors) {
            pred.print(tabNumber + 1);
        }
    }
}
