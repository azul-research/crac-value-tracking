package analysis;


import org.junit.Test;

import java.util.Set;

import static input.JarBytecodeExtractor.analyseJarFile;
import static org.junit.Assert.assertEquals;

public class ClassFileanalyserTest {


    @Test
    public void simpleIfTest() {

        var result = analyseJarFile("src/test/java/examples/simpleif/simpleif.jar");
        System.out.println(result);
        assertEquals(result, Set.of(0, 1));

    }

    @Test
    public void simpleIfElseTest() {
        var result = analyseJarFile("src/test/java/examples/simpleifelse/simpleifelse.jar");
        System.out.println(result);

        assertEquals(result, Set.of(0, 2, 3));
    }

    @Test
    public void nestedIfTest() {
        var result = analyseJarFile("src/test/java/examples/nestedif/nestedif.jar");
        System.out.println(result);

        assertEquals(result, Set.of(0, 1, 4));
    }


    @Test
    public void nestedIf2Test() {
        var result = analyseJarFile("src/test/java/examples/nestedif2/nestedif2.jar");
        System.out.println(result);

        assertEquals(result, Set.of(0, 1, 3, 4));
    }
}
