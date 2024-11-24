package analysis;

import output.CurrentState;

public class MethodAnalyserBase {

    CurrentState result;

    public MethodAnalyserBase(CurrentState result) {
        this.result = result;
    }

    public CurrentState getAnalysisResult() {
        return result;
    }

    public void analyseAllBlocks() {}
}
