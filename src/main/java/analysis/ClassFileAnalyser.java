package analysis;

import org.apache.bcel.Const;
import org.apache.bcel.util.ByteSequence;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static analysis.InstructionsMatcher.*;


public class ClassFileAnalyser {

    Stack<Boolean> stack = new Stack<>();
    HashSet<Integer> unsafeVars = new HashSet<>(Set.of(0));


    public void analyseByteCodeOfMethod(byte[] methodCode) {

        ArrayList<Integer> unsafeCodeLines = new ArrayList<>();


        System.out.println(methodCode.length);

        try (ByteSequence stream = new ByteSequence(methodCode)) {

            int lineNumber = 0;

            while (lineNumber < methodCode.length) {
                short opCode = (short) (methodCode[lineNumber] & 0xff);


                // Find out JVM instruction opcode for this line of code.
//                short opCode = (short) stream.readUnsignedByte();
                System.out.println(opCode);

                var name = Const.getOpcodeName(opCode);
                System.out.println(lineNumber + ": " + name);

                var result = analyseOpCode(name, methodCode, lineNumber);
                unsafeVars.addAll(result.getKey());
                lineNumber = result.getValue();

            }

            System.out.println("Unsafe vars numbers: " + unsafeVars);

        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    Map.Entry<Set<Integer>, Integer> analyseOpCode(String name, byte[] stream, int lineNumber) throws IOException {

        int newLineNumber = lineNumber;
        Set<Integer> newUnsafeVars = new HashSet<>();
        if (matchConstLoad(name)) {
            stack.add(true);
            lineNumber++;
        } else if (matchConstLoadFromPool(name)) {
            stack.add(true);
            lineNumber += 2;
        } else if (matchStoreData(name)) {
            var varNumber = getNumberInOpCode(name);
            if (!stack.pop()) {
                newUnsafeVars.add(varNumber);
            }
            lineNumber++;
        } else if (matchLoadVariable(name)) {
            var varNumber = getNumberInOpCode(name);
            stack.add(!unsafeVars.contains(varNumber));
            lineNumber++;
        } else if (matchBinOperation(name)) {
            var first = stack.pop();
            var second = stack.pop();
            stack.add(first && second);
            lineNumber++;
        } else if (matchLoadArrayElem(name)) {
            stack.pop();
            var isSafe = stack.pop();
            stack.add(isSafe);
            lineNumber++;
        } else if (matchIf(name)) {
            lineNumber = analyseIfBlock(stream, lineNumber, name);
        } else if (!matchReturn(name)) {
            System.out.println("UNKNOWN OPCODE: " + name);
            lineNumber++;
        } else {
            lineNumber++;
        }

        return Map.entry(newUnsafeVars, lineNumber);

    }

    int analyseIfBlock(byte[] stream, int lineNumber, String opCode) throws IOException {
        int branchbyte1 = stream[lineNumber + 1] & 0xff;
        int branchbyte2 = stream[lineNumber + 2] & 0xff;
        int moveTo = (branchbyte1 << 8 | branchbyte2) + lineNumber; // branch to instruction on this line
        var result = analyseIfBranch(stream, lineNumber + 3, moveTo);
        var newUnsafeVars = result.getKey();
        unsafeVars.addAll(newUnsafeVars);
        var lastStepMove = result.getValue();
        if (lastStepMove != moveTo) {
            var result2 = analyseIfBranch(stream, moveTo, lastStepMove);
            unsafeVars.addAll(result2.getKey());
        }

        return moveTo;

    }

    Map.Entry<Set<Integer>, Integer> analyseIfBranch(byte[] stream, int lineNumberStart, int lineNumberEnd) throws IOException {
        Set<Integer> newUnsafeVars = new HashSet<>();

        int i = lineNumberStart;
        while (i < lineNumberEnd) {
            var result = analyseOpCode(Const.getOpcodeName(stream[i] & 0xff), stream, i);
            i = result.getValue();
            newUnsafeVars.addAll(result.getKey());
        }

        int lastStepMove = analyseLastStep(stream, lineNumberEnd - 1);

        return Map.entry(newUnsafeVars, lastStepMove);
    }

    int analyseLastStep(byte[] stream, int lineNumber) {
        if (Const.getOpcodeName(stream[lineNumber]).equals("goto")) {
            int branchbyte1 = stream[lineNumber + 1] & 0xff;
            int branchbyte2 = stream[lineNumber + 2] & 0xff;
            return (branchbyte1 << 8 | branchbyte2);
        }
        return lineNumber + 1;
    }


    int getNumberInOpCode(String opCode) {
        String regex = ".*_(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(opCode);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }


    public static void main(String[] args) {
        byte a = -57;
        System.out.println(a & 0xff);
    }
}
