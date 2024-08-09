package demo;

import java.util.Objects;

class Main {

    static String baseWord = "Hello";
    static String longestWord = "";

    static String password;


    public static void main(String[] secretWords) {
        password = System.getenv("PASSWORD");

        if (secretWords.length < 3) {
            return;
        }

        longestWord = secretWords[0];

        for (int i = 0; i < secretWords.length; i++) {
            if (Objects.equals(secretWords[i], baseWord)) {
                continue;
            }
            int n = secretWords[i].length();
            if (longestWord.length() < n) {
                longestWord = secretWords[i];
            }
        }

    }
}