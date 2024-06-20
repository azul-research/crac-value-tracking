package input.cfg;

import org.apache.bcel.Const;

import java.util.*;

import static analysis.InstructionsMatcher.*;
import static input.JarBytecodeExtractor.getMainMethodCode;

public class ControlFlowGraph {
    static BasicBlock createMethodControlFlowGraph(byte[] methodCode) {


        Map<Integer, BasicBlock> allBlocks = new HashMap<>();
        var blockLeaders = findAllBlockLeaders(methodCode);
        System.out.println(blockLeaders);



        for (int i = 0; i < blockLeaders.size(); i++) {
            int start = blockLeaders.get(i);
            int end;
            if (i == blockLeaders.size() - 1) {
                end = methodCode.length;
            }
            else {
                end = blockLeaders.get(i + 1);
            }
            allBlocks.put(start, new BasicBlock(start, end - 1, Arrays.copyOfRange(methodCode, start, end)));
        }


        for (var entry: allBlocks.entrySet()) {
            var block = entry.getValue();
            if (matchReturn(block.getLastInstruction())) {
                break;
            }
            int successorNumber;
            BasicBlock successor;
            if (matchIf(block.getLastInstruction())) {
                successorNumber = getNextBytes(methodCode, block.getEndLine() - 2) + block.getEndLine() - 2;
                successor = allBlocks.get(successorNumber);
                block.addSuccessor(successor);
                successor.addPredecessor(block);

            }
            successorNumber = block.getEndLine() + 1;
            block.addSuccessor(allBlocks.get(successorNumber));


        }

        return allBlocks.get(0);

    }


    static List<Integer> findAllBlockLeaders(byte[] methodCode) {
        Set<Integer> blockLeaders = new HashSet<>();
        blockLeaders.add(0);
        int line = 0;
        while (line < methodCode.length) {
            String name = getOpCode(methodCode, line);
            System.out.println(line + ": " + name);
            if (matchGoto(name)) {
                blockLeaders.add(getNextBytes(methodCode, line) + line);
                line+= 3;
                blockLeaders.add(line);
            }
            else if (matchIf(name)) {
                blockLeaders.add(getNextBytes(methodCode, line) + line);
                line += 3;
                blockLeaders.add(line);
            }
            else if (matchReturn(name)) {
                blockLeaders.add(line);
                break;
            }
            else if (matchConstLoadFromPool(name)) {
                line += 2;
            }
            else {
                line++;
            }
        }

        return blockLeaders.stream().sorted().toList();
    }

    public static String getOpCode(byte[] methodCode, int number) {
        return Const.getOpcodeName(methodCode[number] & 0xff);
    }

    static int getNextBytes(byte[] methodCode, int number) {
        int branchbyte1 = methodCode[number + 1] & 0xff;
        int branchbyte2 = methodCode[number + 2] & 0xff;
        return (branchbyte1 << 8 | branchbyte2);
    }

    public static void main(String[] args) {
        byte[] code = getMainMethodCode("src/test/java/examples/ifs/simpleifelse/simpleifelse.jar");
        createMethodControlFlowGraph(code);
    }

}
