package entitites.derivatives;

import java.util.Objects;

public class RootDerivative extends DerivativeEntity {
    String name;
    public RootDerivative(String name, DerivativeEntity... derivatives) {
        super(0, derivatives);
        this.name = name;
    }


    @Override
    public String info(int tabNumber) {
        return ("\t".repeat(tabNumber) + "root derivative " + name);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RootDerivative that = (RootDerivative) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
