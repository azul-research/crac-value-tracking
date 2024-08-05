package demo;

import java.sql.SQLOutput;
import java.util.Objects;

class Main {

    static int maxLength;
    static String baseWord = "Hello";
    static String longestWord = "";


    public static void main(String[] args) {
        if (args.length < 3) {
            return;
        }

        System.out.println(System.getenv("CLASS_PATH"));
        System.out.println(1);


        maxLength = args[0].length();
        longestWord = args[0];

        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], baseWord)) {
                continue;
            }
            int n = args[i].length();
            if (maxLength < n) {
                maxLength = n;
                longestWord = args[i];
            }
        }

    }
}