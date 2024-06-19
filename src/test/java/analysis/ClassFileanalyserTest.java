package analysis;


import org.junit.Test;

import static input.JarBytecodeExtractor.analyseJarFile;

public class ClassFileanalyserTest {


    @Test
    public void simpleIfTest() {

        var result = analyseJarFile("src/test/java/examples/simpleif/simpleif.jar");
        System.out.println(result);

    }

    @Test
    public void simpleIfElseTest() {
        var result = analyseJarFile("src/test/java/examples/simpleifelse/simpleifelse.jar");
        System.out.println(result);

    }
}
