package examples.simpleifelse;

public class Main {

    public static void main(String[] args) {
        int a = 0;
        String b = args[1];
        String c = "";
        if (b == null) {
            a = 1;
            c = args[1];
        }
        else {
            a = 2;
            c = "";
        }
    }
}
