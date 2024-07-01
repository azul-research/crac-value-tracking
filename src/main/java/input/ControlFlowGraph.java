package input;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.*;

import java.util.*;

public class ControlFlowGraph {

    private ClassPool pool;

    private Map<String, ClassCFG> classesCFG = new HashMap<>();


    public Map<String, ClassCFG> getCFG() {
        return classesCFG;
    }

    public ClassCFG getClassCFG(String name) {
        return classesCFG.get(name);
    }


    public ControlFlowGraph(String jarFilePath) {

        try {
            pool = ClassPool.getDefault();
            pool.appendClassPath(jarFilePath);
        }
        catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void createClassCFG(String className) {
        try {
            CtClass ctClass = pool.get(className);
            var classCFG = new ClassCFG();
            classesCFG.put(className, classCFG);

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                System.out.println("Analyzing method: " + method.getName());
                classCFG.addMethodCFG(createMethodCFG(method), method.getName());
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        } catch (BadBytecode e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, ControlFlow.Block> createMethodCFG(CtMethod method) throws BadBytecode {

        MethodInfo methodInfo = method.getMethodInfo();
        ControlFlow controlFlow = new ControlFlow(method.getDeclaringClass(), methodInfo);
        ControlFlow.Block[] blocks = controlFlow.basicBlocks();

        // Map to store the control flow graph
        Map<Integer, ControlFlow.Block> controlFlowGraph = new HashMap<>();

        // Build the control flow graph
        for (ControlFlow.Block block : blocks) {
            controlFlowGraph.put(block.position(), block);
        }

        // Print the control flow graph
        for (var block: blocks) {
            System.out.println(block.toString());
        }

        return controlFlowGraph;

    }

//
//
//    public static void analyzeBasicBlock(ControlFlow.Block block, CodeAttribute codeAttribute) throws BadBytecode {
//        CodeIterator codeIterator = codeAttribute.iterator();
//
////        while (codeIterator.hasNext()) {
////            int i = codeIterator.next();
//
//        for (int i = block.position(); i < block.position() + block.length() && codeIterator.hasNext(); i = codeIterator.next()) {
//            int opcode = codeIterator.byteAt(i);
//            System.out.print("Instruction at index " + i + ": " + Mnemonic.OPCODE[opcode]);
//
//            // Handle additional bytes for specific opcodes
//            switch (opcode) {
//                case Opcode.GOTO:
//                case Opcode.IF_ICMPEQ:
//                case Opcode.IF_ICMPNE:
//                case Opcode.IF_ICMPLT:
//                case Opcode.IF_ICMPGE:
//                case Opcode.IF_ICMPGT:
//                case Opcode.IF_ICMPLE:
//                case Opcode.IF_ACMPEQ:
//                case Opcode.IF_ACMPNE:
//                case Opcode.IFNULL:
//                case Opcode.IFNONNULL:
//                case Opcode.IFNE:
//                case Opcode.IFEQ:
//                case Opcode.IFLT:
//                case Opcode.IFGE:
//                case Opcode.IFGT:
//                case Opcode.IFLE:
//                    // These instructions have a 2-byte offset operand
//                    int branchOffset = codeIterator.s16bitAt(i + 1);
//                    System.out.print(", branch offset: " + (branchOffset + block.index() + i));
//                    i += 2; // Skip the additional bytes
//                    break;
//                case Opcode.GOTO_W:
//                    // This instruction has a 4-byte offset operand
//                    int wideBranchOffset = codeIterator.s32bitAt(i + 1);
//                    System.out.print(", wide branch offset: " + wideBranchOffset);
//                    i += 4; // Skip the additional bytes
//                    break;
//                // Add cases for other opcodes with additional bytes as needed
//            }
//
//            System.out.println();
//        }
//    }


}
