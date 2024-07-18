package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import output.CurrentState;

import java.io.IOException;

public class CurrentStateSerializer extends JsonSerializer<CurrentState> {
    @Override
    public void serialize(CurrentState currentState, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {

        try {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("loaded classes", currentState.getLoadedClasses());
            jsonGenerator.writeObjectField("initialised classes", currentState.getInitialisedClasses().keySet());
            jsonGenerator.writeObjectField("static fields", currentState.getInitialisedClasses());
            jsonGenerator.writeObjectField("variables array", currentState.getVariablesArray());
            jsonGenerator.writeEndObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



