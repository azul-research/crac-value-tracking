package entitites;

import java.util.Objects;

public abstract class Entity {
    int line;

    public Entity(int line) {
        this.line = line;
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

    public abstract void print(int tabNumber);


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
