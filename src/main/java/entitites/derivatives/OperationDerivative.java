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


package entitites.derivatives;


import java.util.Arrays;
import java.util.Objects;

public class OperationDerivative extends Derivative {

    Derivative[] predecessors;
    String fileName;

    int line;
    public OperationDerivative(int line, String fileName, Derivative... predecessors) {
        super();
        this.line = line;
        this.fileName = fileName;
        this.predecessors = predecessors;
    }

    public String getFileName() {
        return fileName;
    }


    @Override
    public String info(int tabNumber) {
        StringBuilder result = new StringBuilder();
        result.append("\t".repeat(tabNumber));
        result.append("derivative on line ").append(line).append(" from: \n");
        for (var pred : predecessors) {
            result.append(pred.info(tabNumber + 1));
        }

        return result.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OperationDerivative that = (OperationDerivative) o;
        return line == that.line && Objects.deepEquals(predecessors, that.predecessors) && Objects.equals(fileName, that.fileName);
    }


    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(predecessors), fileName, line);
    }

    public Derivative[] getPredecessors() {
        return predecessors;
    }

    public int getLine() {
        return line;
    }
}

