package analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionsMatcher {


    private static boolean patternMatching(String patternString, String opcode) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(opcode);
        return matcher.matches();
    }

    public static boolean matchIfWith2Arguments(String opcode) {
        return opcode.startsWith("if_");
    }
    public static boolean matchIfWith1Argument(String opcode) {
        return opcode.startsWith("if") && opcode.charAt(2) != '_';
    }

    static boolean matchConstLoad(String opcode) {
        return patternMatching(".const.*", opcode);
    }

    static boolean matchByteLoad(String opcode) {
        return patternMatching("([sb])ipush", opcode);
    }

    public static boolean matchConstLoadFromPool(String opcode) {
        return opcode.startsWith("ldc");
    }

    static boolean matchStoreData(String opcode) {
        return patternMatching(".store_(.*)", opcode);
    }

    static boolean matchStoreToVariable(String opcode) {
        return patternMatching(".store", opcode);
    }

    static boolean matchLoadVariable(String opcode) {
        return patternMatching(".load_(.*)", opcode);
    }

    static boolean matchLoadVariableWithoutIndex(String opcode) {
        return patternMatching(".load", opcode);
    }

    static boolean matchBinOperation(String opcode) {
        return patternMatching(".{1,2}(add|div|mul|sub|shr|shl|rem|or|xor|and)", opcode);
    }

    public static boolean matchStoreToArray(String opcode) {
        return patternMatching(".astore", opcode);
    }

    public static boolean matchReturnValue(String opcode) {
        return patternMatching(".return", opcode);
    }

    static boolean matchLoadArrayElem(String opcode) {
        return patternMatching(".aload", opcode);
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

    public static boolean matchTableSwitch(String opcode) {
        return opcode.equals("tableswitch");
    }

    public static boolean matchThrowError(String opcode) {
        return opcode.equals("athrow");
    }

    public static boolean matchConvertValue(String opcode) {
        return patternMatching(".2.", opcode);
    }

    public static boolean matchSwap(String opcode) {
        return opcode.equals("swap");
    }

    public static int getNumberInOpcode(String opcode) {
        String regex = ".*_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opcode);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
}
