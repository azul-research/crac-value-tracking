package examples.recursionexample;

public class Main {
    public static void main(String[] args) {
        String k = f(args[0]);
    }

    static String f(String a) {
        if (a == "hello") {
            return a;
        }
        else {
            return f(a);
        }
    }
}
