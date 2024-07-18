package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import entitites.derivatives.RootDerivative;

import java.io.IOException;

public class RootDerivativeSerializer extends JsonSerializer<RootDerivative> {
    @Override
    public void serialize(RootDerivative rootDerivative, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        try {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("root derivative", rootDerivative.getName());
//        jsonGenerator.writeStringField("methodName", rootDerivative.methodName);
            jsonGenerator.writeEndObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}