package examples.methodcall;

public class Main {

    public static void main(String[] args) {
        String c = foo(args);
    }

    public static String foo(String[] a) {
        if (a != null) {
            return a[0];
        }
        return null;
    }
}
