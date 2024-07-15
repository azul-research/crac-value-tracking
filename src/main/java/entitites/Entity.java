package entitites;

import java.util.Objects;

public abstract class Entity {
//    int varNumber;

    public Entity() {
//        this.varNumber = varNumber;
    }

    public boolean isUndefined() {
        return this.getClass() == UndefinedEntity.class;
    }

    public boolean isNonDerivative() {
        return this.getClass() == NonDerivative.class;
    }

    public boolean isDerivativeSet() {
        return this.getClass() == DerivativeSet.class;
    }

    public abstract String info(int tabNumber);

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass();
    }

    public int compareType(Entity entity) {
        if (this.getClass() == entity.getClass()) {
            return 0;
        } else if (this.isDerivativeSet() || entity.isUndefined()) {
            return 1;
        } else {
            return -1;
        }
    }

}
