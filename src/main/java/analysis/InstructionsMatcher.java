package analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionsMatcher {

    public static boolean matchIfWith2Arguments(String opCode) {
        return opCode.startsWith("if_");
    }
    public static boolean matchIfWith1Argument(String opCode) {
        return opCode.startsWith("if") && opCode.charAt(2) != '_';
    }

    static boolean matchConstLoad(String opCode) {
        String regex = ".const.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchByteLoad(String opcode) {
        String regex = "([sb])ipush";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opcode);
        return matcher.matches();
    }

    public static boolean matchConstLoadFromPool(String opcode) {
        return opcode.startsWith("ldc");
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
        String regex = ".{1,2}(add|div|mul|sub|shr|shl|rem|or|xor)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    public static boolean matchStoreToArray(String opcode) {
        String regex = ".astore";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opcode);
        return matcher.matches();
    }

    public static boolean matchReturnValue(String opcode) {
        String regex = ".return";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opcode);
        return matcher.matches();
    }

    static boolean matchLoadArrayElem(String opcode) {
        return opcode.equals("aaload");
    }

    static boolean matchLoadLong(String opcode) {
        return opcode.equals("lload");
    }

    public static boolean matchGoto(String opcode) {
        return opcode.startsWith("goto");
    }

    public static boolean matchCreateArray(String opcode) {
        return opcode.equals("anewarray");
    }

    public static boolean matchIncrementLocal(String opcode) {
        return opcode.equals("iinc");
    }

    public static boolean matchInvokeVirtual(String opcode) {
        return opcode.equals("invokevirtual");
    }

    public static boolean matchInvokeSpecial(String opcode) {
        return opcode.equals("invokespecial");
    }

    public static boolean matchInvokeInterface(String opcode) {
        return opcode.equals("invokeinterface");
    }

    public static boolean matchInvokeDynamic(String opcode) {
        return opcode.equals("invokedynamic");
    }

    public static boolean matchInvokeStatic(String opcode) {
        return opcode.equals("invokestatic");
    }


    public static boolean matchGetField(String opcode) {
        return opcode.equals("getfield");
    }

    public static boolean matchPutField(String opcode) {
        return opcode.equals("putfield");
    }

    public static boolean matchGetStatic(String opcode) {
        return opcode.equals("getstatic");
    }

    public static boolean matchPutStatic(String opcode) {
        return opcode.equals("putstatic");
    }

    public static boolean matchGetArrayLength(String opcode) {
        return opcode.equals("arraylength");
    }

    public static boolean matchReturnVoid(String opcode) {
        return opcode.equals("return");
    }

    public static boolean matchNew(String opcode) {
        return opcode.equals("new");
    }

    public static boolean matchDuplicateValue(String opcode) {
        return opcode.equals("dup");
    }

    public static boolean matchPop(String opcode) {
        return opcode.equals("pop");
    }

    public static boolean matchPop2(String opcode) {
        return opcode.equals("pop2");
    }

    public static boolean matchMonitor(String opcode) {
        return opcode.startsWith("monitor");
    }

    public static int getNumberInOpCode(String opCode) {
        String regex = ".*_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
}
