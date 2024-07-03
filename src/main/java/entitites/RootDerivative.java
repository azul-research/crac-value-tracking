package entitites;

public class RootDerivative extends DerivativeEntity {
    String name;
    public RootDerivative(String name, DerivativeEntity... derivatives) {
        super(0, derivatives);
        this.name = name;
    }


    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "root derivative " + name);
    }



}
