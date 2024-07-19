import analysis.Analyser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import entitites.Entity;
import entitites.derivatives.OperationDerivative;
import entitites.derivatives.RootDerivative;
import serializers.EntitySerializer;
import serializers.OperationDerivativeSerializer;
import serializers.RootDerivativeSerializer;
import output.CurrentState;
import serializers.CurrentStateSerializer;


import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Objects;


public class UnsafeCodeAnalysis {
    public static void main(String[] args) throws IOException {

        if (args.length < 3) {
            System.out.println("""
                    Not enough arguments:\s
                    first argument - path to .jar file,\s
                    second argument - full name of Main class,\s
                    third argument - path to Java""");
            return;
        }

        Analyser analyser = new Analyser(args[0], args[1]);

        CurrentState result = analyser.analyseProgram();

        saveToJson(result, "result.json");

//        analyseMethod(getSourceFile("java"), "java.lang.String", "contentEquals", List.of(0, 1));
//        analyseMethod(args[0], args[1], "main", List.of(0));

    }


    public static void saveToJson(CurrentState result, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RootDerivative.class, new RootDerivativeSerializer());
        module.addSerializer(OperationDerivative.class, new OperationDerivativeSerializer());
        module.addSerializer(CurrentState.class, new CurrentStateSerializer());
        module.addSerializer(Entity.class, new EntitySerializer());
        mapper.registerModule(module);

        try {
            mapper.writeValue(new File(fileName), result);
            System.out.println("JSON file created: currentState.json");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getClassName(String classPath) {
        String[] splittedPath = classPath.split("/");
        if (splittedPath.length < 2) {
            System.out.println("INCORRECT CLASS PATH");
            return null;
        }
        int i = 0;

        while (i < splittedPath.length && !Objects.equals(splittedPath[i], "java")) {
            i++;
        }
        if (i == splittedPath.length) {
            System.out.println("INCORRECT CLASS PATH");
            return null;
        }

        splittedPath = Arrays.copyOfRange(splittedPath, i + 1, splittedPath.length);

        String result = String.join(".", splittedPath);
        return result.replace(".class", "");


    }

}
