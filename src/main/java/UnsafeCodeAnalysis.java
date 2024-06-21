import input.JarBytecodeExtractor;

import java.util.Objects;

import static input.ControlFlowGraphCreation.createClassCFG;

public class UnsafeCodeAnalysis {
    public static void main(String[] args) {

        String jarFilePath = "/Users/dariasuvorova/IdeaProjects/unsafecodeanalysis/src/test/java/examples/ifs/simpleif/simpleif.jar";
        JarBytecodeExtractor extractor = new JarBytecodeExtractor(jarFilePath);
        var classNames = extractor.getClassNames();

        for (var className : classNames) {
            System.out.println(className);
            String[] splittedClassName = className.split("\\.");
            StringBuilder builder = new StringBuilder();
            int i = 0;
            while (!Objects.equals(splittedClassName[i], "java")) {
                i++;
            }
            i++;
            builder.append(splittedClassName[i]);
            i++;
            while (i < splittedClassName.length) {
                builder.append(".");
                builder.append(splittedClassName[i]);
                i++;
            }
            createClassCFG(className, jarFilePath);
        }


    }
}
