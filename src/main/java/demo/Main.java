package demo;

public class Main {

    static String mainField = "";
    static int argumentsNumber = 0;

    public static void main(String[] args) {
        argumentsNumber = args.length;

        if (argumentsNumber > 2) {
            mainField = args[2];
        }
        else {
            mainField = args[0];
        }
    }
}
