/*
 * Copyright 2024 Azul Systems, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


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

    public static boolean matchDuplicateValue2(String opcode) {
        return opcode.equals("dup2");
    }

    public static boolean matchDuplicate1(String opcode) {
        return opcode.equals("dup_x1");
    }

    public static boolean matchDuplicate2(String opcode) {
        return opcode.equals("dup_x2");
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
