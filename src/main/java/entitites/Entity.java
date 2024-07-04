package entitites;

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



}
