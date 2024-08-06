package entitites.derivatives;

import java.util.Objects;

public abstract class Derivative {
    String className;


    public String getClassName() {
        return className;
    }

    Derivative() {}
    public abstract String info(int tabNumber);

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(className);
    }
}
