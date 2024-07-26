package examples.switchexample;

public class Main {

    public static void main(String[] args) {
        tableSwitch(7);
    }

    static public String tableSwitch(int value) {
        return switch (value) {
            case 5 -> "five";
            case 6 -> "six";
            case 8 -> "eight";
            default -> "number";
        };
    }



}
