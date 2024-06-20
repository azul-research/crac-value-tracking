package examples.ifs.simpleifelse;

public class Main {

    public static void main(String[] args) {
        int a = 0;
        String b = args[1];
        String c = "";
        if (b == null) {
            a = 1;
            c = "";
        }
        else {
            a = 2;
            c = args[1];
        }
    }
}
