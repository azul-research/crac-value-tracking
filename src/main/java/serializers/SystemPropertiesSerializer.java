package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import entitites.derivatives.SystemPropertyRoot;

import java.io.IOException;

public class SystemPropertiesSerializer extends JsonSerializer<SystemPropertyRoot> {
    @Override
    public void serialize(SystemPropertyRoot envVariableRoot, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        try {
//            jsonGenerator.writeStartObject();
//            jsonGenerator.writeStringField("", rootDerivative.getName());
////        jsonGenerator.writeStringField("methodName", rootDerivative.methodName);
//            jsonGenerator.writeEndObject();

            jsonGenerator.writeString("systemProperty");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
