package entitites.derivatives;

import java.util.Objects;

public class RootDerivative extends Derivative {
    String name;
    public RootDerivative(String name) {
        super();
        this.name = name;
    }


    @Override
    public String info(int tabNumber) {
        return "\t".repeat(tabNumber) + "root derivative " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RootDerivative that = (RootDerivative) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
