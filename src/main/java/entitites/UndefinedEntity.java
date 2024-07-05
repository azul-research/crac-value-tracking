package entitites;

public class UndefinedEntity extends Entity {
    public UndefinedEntity() {
        super(0);
    }


    @Override
    public String info(int tabNumber) {
        return  "\t".repeat(tabNumber) + "undefined";
    }
}
