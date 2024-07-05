package entitites.derivatives;

import entitites.Entity;

public class StraightDerivative extends DerivativeEntity {

    int codeLine;
    public StraightDerivative(int line, Entity derivatives) {
        super(line, derivatives);
        codeLine = line;
    }

    @Override
    public String info(int tabNumber) {

        return "\t".repeat(tabNumber) + "assigned to derivative on line " + codeLine + ":\n" +
                this.predecessors[0].info(tabNumber + 1);
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
