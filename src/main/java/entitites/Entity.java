package entitites;

import java.util.Objects;

public class Entity {
    protected int line;
//    int varNumber;

    public Entity(int line) {
        this.line = line;
//        this.varNumber = varNumber;
    }

    public boolean isUndefined() {
        return this.getClass() == UndefinedEntity.class;
    }

    public boolean isNonDerivative() {
        return this.getClass() == NonDerivativeEntity.class;
    }

    public boolean isDerivative() {
        return !isUndefined() && !isNonDerivative();
    }

    public String info(int tabNumber) {
        return "";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return line == entity.line;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(line);
    }
}
