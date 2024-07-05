package entitites.derivatives;

import entitites.Entity;

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
    public String info(int tabNumber) {
        StringBuilder result = new StringBuilder();
        result.append("\t".repeat(tabNumber)).append("derivative from any of this: \n");
        for (var pred : predecessors) {
            result.append(pred.info(tabNumber + 1));
            result.append("\n");
        }

        return result.toString();
    }
}
