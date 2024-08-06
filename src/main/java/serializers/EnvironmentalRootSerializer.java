package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import entitites.derivatives.EnvironmentalRoot;

import java.io.IOException;

public class EnvironmentalRootSerializer extends JsonSerializer<EnvironmentalRoot> {
    @Override
    public void serialize(EnvironmentalRoot envVariableRoot, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        try {
//            jsonGenerator.writeStartObject();
//            jsonGenerator.writeStringField("", rootDerivative.getName());
////        jsonGenerator.writeStringField("methodName", rootDerivative.methodName);
//            jsonGenerator.writeEndObject();

            jsonGenerator.writeString("environmentalVariable");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
