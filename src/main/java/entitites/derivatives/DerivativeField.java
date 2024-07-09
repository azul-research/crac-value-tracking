package entitites.derivatives;

import entitites.Entity;

public class DerivativeField extends DerivativeEntity{
    public DerivativeField(int line, Entity... derivatives) {
        super(line, derivatives);
    }

    @Override
    public String info(int tabNumber) {
        return "\t".repeat(tabNumber) + "Field of derivative: " + predecessors[0].info(tabNumber + 1);
    }
}
