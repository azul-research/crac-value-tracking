package entitites.derivatives;

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
}
