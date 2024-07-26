package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import output.ClassStaticFields;

import java.io.IOException;

public class ClassStaticFieldsSerializer extends JsonSerializer<ClassStaticFields> {
    @Override
    public void serialize(ClassStaticFields staticFields, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        for (var name : staticFields.getStaticFields().keySet()) {
            jsonGenerator.writeObjectField(name, staticFields.getField(name));
        }

        jsonGenerator.writeEndObject();
    }
}
