package examples.ifandwhile;

public class Main {
    public static void main(String[] args) {
        int a = 3;
        String s = "";
        String t = args[0];
        while (a > 0) {
            a--;
            if (a == 5) {
                if (a == 1) {
                    s = args[0];
                }
                else {
                    s = t;
                }
            }
        }
    }
}
