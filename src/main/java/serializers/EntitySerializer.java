package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import entitites.Entity;

import java.io.IOException;

public class EntitySerializer extends JsonSerializer<Entity> {
    @Override
    public void serialize(Entity entity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try {
//            jsonGenerator.writeStartObject();
//            jsonGenerator.writeObjectField("type", entity.getType());

            if (entity.isDerivativeSet()) {
                jsonGenerator.writeObject(entity.getDerivativeSet());
            }
            else {
                jsonGenerator.writeString("NON_DERIVATIVE");
            }

//            jsonGenerator.writeEndObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
