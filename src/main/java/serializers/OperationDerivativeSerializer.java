package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import entitites.derivatives.OperationDerivative;

import java.io.IOException;

public class OperationDerivativeSerializer extends JsonSerializer<OperationDerivative> {
    @Override
    public void serialize(OperationDerivative operationDerivative, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("line", operationDerivative.getLine());
            jsonGenerator.writeObjectField("derivative from", operationDerivative.getPredecessors());
//        jsonGenerator.writeStringField("methodName", rootDerivative.methodName);
            jsonGenerator.writeEndObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
