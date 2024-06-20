package analysis;


import org.junit.Test;

import java.util.Set;

import static input.JarBytecodeExtractor.analyseJarFile;
import static org.junit.Assert.assertEquals;

public class ClassFileanalyserTest {


    @Test
    public void simpleIfTest() {

        var result = analyseJarFile(getJarFilePath("simpleif"));
        System.out.println(result);
        assertEquals(result, Set.of(0, 1));

    }

    @Test
    public void simpleIfElseTest() {
        var result = analyseJarFile(getJarFilePath("simpleifelse"));
        System.out.println(result);

        assertEquals(result, Set.of(0, 2, 3));
    }

    @Test
    public void nestedIfTest() {
        var result = analyseJarFile(getJarFilePath("nestedif"));
        System.out.println(result);

        assertEquals(Set.of(0, 1, 4), result);
    }


    @Test
    public void nestedIf2Test() {
        var result = analyseJarFile(getJarFilePath("nestedif2"));
        System.out.println(result);

        assertEquals(Set.of(0, 1, 2, 3, 4), result);
    }

    @Test
    public void simpleAssignmentsTest() {
        var result = analyseJarFile(getJarFilePath("simpleassignments"));

        assertEquals(Set.of(0, 2), result);
    }

    @Test
    public void nestedIf3Test() {
        var result = analyseJarFile(getJarFilePath("nestedif3"));

        assertEquals(Set.of(0, 1, 2, 3, 4), result);
    }



    private String getJarFilePath(String name) {
        return "src/test/java/examples/"+ name + "/"+ name + ".jar";
    }



}
