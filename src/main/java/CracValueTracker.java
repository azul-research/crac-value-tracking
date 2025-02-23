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


import analysis.Analyser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import entitites.Entity;
import entitites.derivatives.EnvironmentalRoot;
import entitites.derivatives.OperationDerivative;
import entitites.derivatives.RootDerivative;
import entitites.derivatives.SystemPropertyRoot;
import output.ClassStaticFields;
import output.ReverseJson;
import serializers.*;
import output.CurrentState;


import java.io.File;
import java.io.IOException;


public class CracValueTracker {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("""
                    Not enough arguments:\s
                    first argument - path to .jar file,\s
                    second argument - full name of Main class,\s
                    third argument - file name for backward analysis,\s
                    fourth argument - file name for forward analysis""");
            return;
        }
        var jarPath = args[0];
        var mainClass = args[1];
        var fileNameBackward = args[2];
        var fileNameForward = args[3];

        Analyser analyser = new Analyser(jarPath, mainClass);

        CurrentState result = analyser.analyseProgram();

        saveToJson(result, fileNameBackward);

        ReverseJson.main(new String[]{fileNameBackward, fileNameForward});

    }


    public static void saveToJson(CurrentState result, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RootDerivative.class, new RootDerivativeSerializer());
        module.addSerializer(EnvironmentalRoot.class, new EnvironmentalRootSerializer());
        module.addSerializer(SystemPropertyRoot.class, new SystemPropertiesSerializer());
        module.addSerializer(OperationDerivative.class, new OperationDerivativeSerializer());
        module.addSerializer(CurrentState.class, new CurrentStateSerializer());
        module.addSerializer(Entity.class, new EntitySerializer());
        module.addSerializer(ClassStaticFields.class, new ClassStaticFieldsSerializer());
        mapper.registerModule(module);

        try {
            mapper.writeValue(new File(fileName), result);
            System.out.println("JSON file created: " + fileName);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
