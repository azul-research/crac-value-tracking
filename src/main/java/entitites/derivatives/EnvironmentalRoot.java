package entitites.derivatives;

public class EnvironmentalRoot extends Derivative {
    @Override
    public String info(int tabNumber) {
        return "environmental variable";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
