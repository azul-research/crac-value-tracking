package analysis;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;

public class MyCodeIterator extends CodeIterator {
    protected MyCodeIterator(CodeAttribute ca) {
        super(ca);
    }

    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};

    int myNextOpcode(byte[] code, int index) {
        int opcode;
        opcode = code[index] & 255;

        int len = opcodeLength[opcode];
        if (len > 0) {
            return index + len;
        }

        if (opcode == 196) {
            if (code[index + 1] == -124) {
                return index + 6;
            }

            return index + 4;
        }

        int index2 = (index & -4) + 8;
        int low;
        if (opcode == 171) {
            low = ByteArray.read32bit(code, index2);
            return index2 + low * 8 + 4;
        }

        if (opcode == 170) {
            low = ByteArray.read32bit(code, index2);
            int high = ByteArray.read32bit(code, index2 + 4);
            return index2 + (high - low + 1) * 4 + 8;
        }

        return -1;
    }
}
