package analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionsMatcher {

    public static boolean matchIf(String opCode) {
        return opCode.startsWith("if");
    }

    static boolean matchConstLoad(String opCode) {
        String regex = ".const.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    public static boolean matchConstLoadFromPool(String opcode) {
        return opcode.equals("ldc");
    }

    static boolean matchStoreData(String opCode) {
        String regex = ".store_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchStoreToVariable(String opCode) {
        String regex = ".store";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchLoadVariable(String opCode) {
        String regex = ".load_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchBinOperation(String opCode) {
        String regex = ".{1,2}(add|div|mul|sub)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }


    static boolean matchLoadArrayElem(String opcode) {
        return opcode.equals("aaload");
    }

    public static boolean matchReturn(String opCode) {
        return opCode.equals("return");
    }

    public static boolean matchGoto(String opcode) {
        return opcode.equals("goto");
    }

    public static boolean matchCreateArray(String opcode) {
        return opcode.equals("anewarray");
    }

    public static boolean matchStoreToArray(String opcode) {
        return opcode.equals("aastore");
    }

    public static boolean matchIncrementLocal(String opcode) {
        return opcode.equals("iinc");
    }
}
