package examples.example1;

public class Main {

    static public int a = 12;

    public static void main(String[] args) {
        String b = "";
        String c = "";
        int i = 5;
        if (i > 0) {
            i++;
            if (i == 5) {
                b = args[i];
            }
            else {
                c = args[i];
            }
        }
    }
}
