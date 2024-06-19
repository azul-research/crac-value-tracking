package analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionsMatcher {

    static boolean matchIf(String opCode) {
        return opCode.startsWith("if");
    }

    static boolean matchConstLoad(String opCode) {
        String regex = ".const.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchConstLoadFromPool(String opcode) {
        return opcode.equals("ldc");
    }

    static boolean matchStoreData(String opCode) {
        String regex = ".store_(.*)";
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

    static boolean matchBinOperation(String opcode) {
        return opcode.equals("iadd") || opcode.equals("imull");
    }


    static boolean matchLoadArrayElem(String opcode) {
        return opcode.equals("aaload");
    }

    static boolean matchReturn(String opCode) {
        return opCode.equals("return");
    }
}
