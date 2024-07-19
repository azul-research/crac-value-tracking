package entitites;

import entitites.derivatives.Derivative;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static entitites.Entity.Type.DERIVATIVE_SET;

public class Entity {

    public void mergeWithDerivativeSet(Entity entity) {
        if (!isDerivativeSet() || !entity.isDerivativeSet()) {
            return;
        }
        derivativeSet.addAll(entity.derivativeSet);
    }


    public static Entity createNonDerivative() {
        return new Entity(Entity.Type.NON_DERIVATIVE);
    }

    public enum Type {
        UNDEFINED,
        NON_DERIVATIVE,
        DERIVATIVE_SET
    }

    Type type;


    Set<Derivative> derivativeSet = new HashSet<>();

//    int varNumber;

    public Entity(Type type) {
        this.type = type;
    }

    public Entity(Type type, Derivative... derivatives) {
        this.type = type;
        derivativeSet.addAll(List.of(derivatives));
    }


    public Set<Derivative> getDerivativeSet() {
        if (type != DERIVATIVE_SET) {
            return null;
        }
        return derivativeSet;
    }

    public void addDerivative(Derivative derivative) {
        if (type != DERIVATIVE_SET) {
            return;
        }
        derivativeSet.add(derivative);
    }

    public void setDerivativeSet(Set<Derivative> derivativeSet) {
        if (type != DERIVATIVE_SET) {
            return;
        }
        this.derivativeSet = derivativeSet;
    }

    public boolean isUndefined() {
        return type == Type.UNDEFINED;
    }

    public boolean isNonDerivative() {
        return type == Type.NON_DERIVATIVE;
    }

    public boolean isDerivativeSet() {
        return type == DERIVATIVE_SET;
    }

    public void setToDerivative() {
        type = DERIVATIVE_SET;
    }


    public Type getType() {
        return type;
    }

    public String info(int tabNumber) {
        if (isUndefined()) {
            return "\t".repeat(tabNumber) + "undefined";
        }
        if (isNonDerivative()) {
            return  "\t".repeat(tabNumber) + "assigned to non derivative";
        }
        else {
            return infoDerivative(tabNumber);
        }
    }


    private String infoDerivative(int tabNumber) {
        if (derivativeSet.size() == 1) {
            return derivativeSet.iterator().next().info(tabNumber);
        }

        StringBuilder result = new StringBuilder();
        result.append("/t".repeat(tabNumber));
        result.append("Derivative from any of this:\n");

        for (Derivative predecessor : derivativeSet) {
            result.append(predecessor.info(tabNumber + 1));
            result.append("\n");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return type == entity.type && Objects.equals(derivativeSet, entity.derivativeSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, derivativeSet);
    }

    public int compareType(Entity entity) {
        return type.compareTo(entity.type);
    }

}
