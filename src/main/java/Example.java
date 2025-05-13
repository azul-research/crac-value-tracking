

public class Example {
    private static String password;

    public static void main(String[] args) {
        someFunction();
        password = args[0];
        someFunction();
        someFunction();
    }

    public static void someFunction() {
        int b = 12;
        for (int i = 0; i < 10; i++) {
            b += i;
        }
    }
}




