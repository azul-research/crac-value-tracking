package examples.nestedif;

public class Main {
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
