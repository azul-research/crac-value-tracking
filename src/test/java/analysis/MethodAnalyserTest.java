package analysis;

import entitites.Entity;
import entitites.derivatives.OperationDerivative;
import entitites.derivatives.RootDerivative;
import input.ControlFlowGraph;

import org.junit.jupiter.api.Test;
import output.CurrentState;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MethodAnalyserTest {


    @Test
    public void simpleAnalysisTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/main3.jar", "examples.example3.Main");
        Entity[] expected = new Entity[3];

        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.NON_DERIVATIVE);
        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET , new OperationDerivative(6, new RootDerivative("args")));
        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithIfTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/main5.jar", "examples.example5.Main");
        Entity[] expected = new Entity[3];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(9, new RootDerivative("args")));
        expected[2] = new Entity(Entity.Type.NON_DERIVATIVE);
        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithWhileTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/main2.jar", "examples.example2.Main");
        Entity[] expected = new Entity[3];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.NON_DERIVATIVE);
        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(9, new RootDerivative("args")));

        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithNestedIfTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/main1.jar", "examples.example1.Main");
        Entity[] expected = new Entity[4];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(11, new RootDerivative("args")));
        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(14, new RootDerivative("args")));
        expected[3] = new Entity(Entity.Type.NON_DERIVATIVE);
        assertArrayEquals(expected, result.getVariablesArray());

    }

    @Test
    public void analysisWithIfsAndWhiles() {
        var result = analyseMainFunction("src/test/java/testJarFiles/main7.jar", "examples.example7.Main");
        Entity[] expected = new Entity[4];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.NON_DERIVATIVE);

        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(12, new RootDerivative("args")));
        expected[2].addDerivative(new OperationDerivative(15, new OperationDerivative(7, new RootDerivative("args"))));

        expected[3] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(7, new RootDerivative("args")));
        assertArrayEquals(expected, result.getVariablesArray());

    }


    public CurrentState analyseMainFunction(String jarFilePath, String className) {
        ControlFlowGraph cfg = new ControlFlowGraph(jarFilePath);
        cfg.createClassCFG(className);
        String methodName = "main";
        var methodCFG = cfg.getClassCFG(className).getMethodCFG(methodName);
        var method = cfg.getMethod(className, methodName);

        Analyser analyser = new Analyser(jarFilePath, className);
        var result = analyser.analyseProgram();

//        MethodAnalyser analyser = new MethodAnalyser(methodCFG, method, List.of(0));

        return result.getAnalysisResult();


    }
}
