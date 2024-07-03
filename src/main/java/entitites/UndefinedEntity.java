package entitites;

public class UndefinedEntity extends Entity {
    public UndefinedEntity() {
        super(0);
    }


    @Override
    public void print(int tabNumber) {
        System.out.println("\t".repeat(tabNumber) + "undefined");
    }
}
