package examples.example7;

public class Main {
    public static void main(String[] args) {
        int a = 3;

        String s = "";
        String t = args[0];

        while (a > 0) {
            a--;

            if (a == 5) {
                if (a == 1) {
                    s = f(args[0]);
                }
                else {
                    s = t;
                }
            }
        }
    }


    static String f(String a) {
        String b = a;
        return b;
    }
}
