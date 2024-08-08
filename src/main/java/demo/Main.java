package demo;

import java.util.Objects;

class Main {

    static String baseWord = "Hello";
    static String longestWord = "";

    static int passwordLength;
    static String password;


    public static void main(String[] args) {
        if (args.length < 3) {
            return;
        }
        password = System.getenv("PASSWORD");
        passwordLength = password.length();

        longestWord = args[0];

        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], baseWord)) {
                continue;
            }
            int n = args[i].length();
            if (longestWord.length() < n) {
                longestWord = args[i];
            }
        }

    }
}