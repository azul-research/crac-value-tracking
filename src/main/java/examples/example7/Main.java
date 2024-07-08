package examples.example7;

public class Main {
    public static void main(String[] args) {
        int a = 3;

        String s = "";

        while (a > 0) {
            a--;

            if (a == 5) {
                s = args[0];
            }
        }
    }
}
