package examples.simplestaticfield;

public class Main {
    static String field;


    public static void main(String[] args) {
        String a = args[0];
        int i = 0;
        if (i > 0) {
            field = a;
        }
        else {
            field = args[1];
        }
    }

}
