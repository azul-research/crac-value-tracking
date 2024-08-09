package analysis;

import entitites.Entity;
import entitites.derivatives.OperationDerivative;
import entitites.derivatives.RootDerivative;

import org.junit.jupiter.api.Test;
import output.ClassStaticFields;
import output.CurrentState;
import output.MyClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodAnalyserTest {


    @Test
    public void simpleAnalysisTest() {

        var fileName = "examples.simple.Main";
        var result = analyseMainFunction("src/test/java/testJarFiles/mainsimple.jar", "examples.simple.Main");
        Entity[] expected = new Entity[3];

        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.NON_DERIVATIVE);
        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(6, "Main.java", new RootDerivative("args")));
        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithIfTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/mainif.jar", "examples.ifexample.Main");
        Entity[] expected = new Entity[3];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(9, "Main.java", new RootDerivative("args")));
        expected[2] = new Entity(Entity.Type.NON_DERIVATIVE);
        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithWhileTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/mainwhile.jar", "examples.whileexample.Main");
        Entity[] expected = new Entity[3];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.NON_DERIVATIVE);
        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(9,"Main.java", new RootDerivative("args")));

        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithNestedIfTest() {
        var result = analyseMainFunction("src/test/java/testJarFiles/mainnestedif.jar", "examples.nestedif.Main");
        Entity[] expected = new Entity[4];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(11, "Main.java", new RootDerivative("args")));
        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(14, "Main.java", new RootDerivative("args")));
        expected[3] = new Entity(Entity.Type.NON_DERIVATIVE);
        assertArrayEquals(expected, result.getVariablesArray());

    }

    @Test
    public void analysisWithIfsAndWhiles() {
        var result = analyseMainFunction("src/test/java/testJarFiles/mainifandwhile.jar", "examples.ifandwhile.Main");
        Entity[] expected = new Entity[4];
        expected[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expected[1] = new Entity(Entity.Type.NON_DERIVATIVE);

        expected[2] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(12, "Main.java", new RootDerivative("args")));
        expected[2].addDerivative(new OperationDerivative(15, "Main.java", new OperationDerivative(7, "Main.java", new RootDerivative("args"))));

        expected[3] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(7, "Main.java", new RootDerivative("args")));
        assertArrayEquals(expected, result.getVariablesArray());
    }


    @Test
    public void analysisWithStaticField() {
        var result = analyseMainFunction("src/test/java/testJarFiles/mainstaticfields.jar", "examples.staticfields.Main");

        Entity[] expectedVariables = new Entity[3];
        expectedVariables[0] = new Entity(Entity.Type.DERIVATIVE_SET, new RootDerivative("args"));
        expectedVariables[1] = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(7, "Main.java", new RootDerivative("args")));
        expectedVariables[2] = new Entity(Entity.Type.NON_DERIVATIVE);

        assertArrayEquals(expectedVariables, result.getVariablesArray());

        MyClass myClassMain = new MyClass("examples.staticfields.Main");
        MyClass myClassBase = new MyClass("examples.staticfields.Base");
        MyClass myClassObj = new MyClass("java.lang.Object");

        Set<MyClass> expectedLoadedClasses = Set.of(myClassMain, myClassBase, myClassObj);
        assertEquals(expectedLoadedClasses, result.getLoadedClasses());

        Map<MyClass, ClassStaticFields> expectedInitialisedClasses = new HashMap<>();

        expectedInitialisedClasses.put(myClassObj, new ClassStaticFields(myClassObj));

        expectedInitialisedClasses.put(myClassMain, new ClassStaticFields(myClassMain));
        Entity staticFieldEntity = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(12, "Main.java", new RootDerivative("args")), new OperationDerivative(15, "Main.java", new OperationDerivative(7, "Main.java", new RootDerivative("args"))));
        expectedInitialisedClasses.get(myClassMain).addStaticField("staticField", staticFieldEntity);

        expectedInitialisedClasses.put(myClassBase, new ClassStaticFields(myClassBase));
        Entity fieldEntity = new Entity(Entity.Type.DERIVATIVE_SET, new OperationDerivative(18, "Main.java", new RootDerivative("args")));
        expectedInitialisedClasses.get(myClassBase).addStaticField("field", fieldEntity);

        assertEquals(expectedInitialisedClasses, result.getInitialisedClasses());
    }

    @Test
    public void analysisWithManyClasses() {

    }

    public CurrentState analyseMainFunction(String jarFilePath, String className) {
        Analyser analyser = new Analyser(jarFilePath, className);
        return analyser.analyseProgram();
    }


}
