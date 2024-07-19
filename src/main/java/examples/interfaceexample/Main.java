package examples.interfaceexample;

public class Main {
    public static void main(String[] args) {
        String[] array = new String[5];

        array[0] = args[0];
        for (int i = 1; i < 5; i++) {
            array[i] = array[i - 1];
        }
    }
}
