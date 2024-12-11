package analysis;

import entitites.Entity;
import javassist.CtBehavior;
import output.CurrentState;
import output.JVMState;

import java.util.Map;

public class MethodAnalyserBase {

    CurrentState result;

    public MethodAnalyserBase(CurrentState result) {
        this.result = result;
    }

    public MethodAnalyserBase(CtBehavior method,  Map<Integer, Entity> derivativeVars, JVMState jvmState) {
        var codeAttribute = method.getMethodInfo().getCodeAttribute();
        var localVariablesNumber = derivativeVars.size();
        if (codeAttribute != null) {
            localVariablesNumber = codeAttribute.getMaxLocals();
        }
        this.result = CurrentState.getEmptyState(localVariablesNumber, derivativeVars, jvmState);
    }

    public CurrentState getAnalysisResult() {
        return result;
    }

    public void analyseAllBlocks() {}
}
