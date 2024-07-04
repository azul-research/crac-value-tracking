package entitites;

import java.util.Arrays;

public class StraightDerivative extends DerivativeEntity {
    public StraightDerivative(int line, Entity derivatives) {
        super(line, derivatives);
    }

    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "assigned to derivative on line " + line + ":");
        this.predecessors[0].print(tabNumber + 1);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StraightDerivative that = (StraightDerivative) o;
        return super.equals(that);
    }
}
