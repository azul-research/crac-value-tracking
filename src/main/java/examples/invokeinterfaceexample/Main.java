package examples.invokeinterfaceexample;

public class Main {
    public static void main(String[] args) {
        Base b;
        int i = 0;
        if (i > 0) {
            b = new Foo();
        }
        else {
            b = new Bar();
        }

        String c = b.method(args[0]);
    }
}
