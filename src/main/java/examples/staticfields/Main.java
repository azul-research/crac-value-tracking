package examples.staticfields;

public class Main {
    static String staticField;

    public static void main(String[] args) {
        String a = args[0];

        int i = 0;

        if (i > 0) {
            staticField = "";
        }
        else {
            staticField = a;
        }

        Base.field = args;
    }

}
