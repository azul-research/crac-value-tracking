package entitites;

public class UndefinedEntity extends Entity {
    public UndefinedEntity() {
        super();
    }


    @Override
    public String info(int tabNumber) {
        return  "\t".repeat(tabNumber) + "Undefined";
    }
}
