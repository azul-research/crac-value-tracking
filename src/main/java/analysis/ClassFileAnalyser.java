package analysis;

import org.apache.bcel.Const;
import org.apache.bcel.util.ByteSequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassFileAnalyser {

    public static void analyseByteCodeOfMethod(byte[] methodCode) {

        HashSet<Integer> unsafeVars = new HashSet<>(Set.of(0));
        HashSet<Integer> unknownVars = new HashSet<>(Set.of(0));
        Stack<Boolean> stack = new Stack<>();
        ArrayList<Integer> unsafeCodeLines = new ArrayList<>();

        try (ByteSequence stream = new ByteSequence(methodCode)) {

            for (int i = 0; stream.available() > 0; i++) {

                // Get line number
                int lineNumber = stream.getIndex();

                // Find out JVM instruction opcode for this line of code.
                short opCode = (short) stream.readUnsignedByte();

                var name = Const.getOpcodeName(opCode);
                System.out.println(lineNumber + ": " + name);

                analyseOpCode(name, stack, unsafeVars);

            }

            System.out.println("Unsafe vars numbers: " + unsafeVars);
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    static void analyseOpCode(String name, Stack<Boolean> stack, Set<Integer> unsafeVars) {

        if (matchConstStore(name)) {
            stack.add(true);
        }
        else if (matchStoreVariable(name)) {
            var varNumber = getNumberInOpCode(name);
            if (!stack.pop()) {
                unsafeVars.add(varNumber);
            }
        }
        else if (matchLoadVariable(name)) {
            var varNumber = getNumberInOpCode(name);
            stack.add(!unsafeVars.contains(varNumber));
        }
        else if (matchBinOperation(name)) {
            var first = stack.pop();
            var second = stack.pop();
            stack.add(first && second);
        }
        else if (matchLoadArrayElem(name)) {
            stack.pop();
            var isSafe = stack.pop();
            stack.add(isSafe);
        }
        else if (!matchReturn(name)){
            System.out.println("UNKNOWN OPCODE: " + name);
        }

    }

    static void analyseIfBlock() {

    }




    static boolean matchConstStore(String opCode) {
        String regex = ".const.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchStoreVariable(String opCode) {
        String regex = "astore_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        return matcher.matches();
    }

    static boolean matchLoadVariable(String opCode) {
        String regex = "aload_(.*)";
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

    static int getNumberInOpCode(String opCode) {
        String regex = ".*_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }


    public static void main(String[] args) {
        matchStoreVariable("astore_1");
    }
}
