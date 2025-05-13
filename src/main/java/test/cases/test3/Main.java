package test.cases.test3;

public class Main {

    static int st;
    public static void main(String[] args) {
        try {
            int a = args.length;
            int b = args[0].charAt(1);
            st = b;
        }
        catch (NumberFormatException e) {
            //hello
        }
    }
}
