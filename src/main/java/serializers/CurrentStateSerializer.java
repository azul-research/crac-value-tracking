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
//            jsonGenerator.writeObjectField("loadedClasses", currentState.getLoadedClasses().stream().map(MyClass::getFullName).toArray());
//            jsonGenerator.writeObjectField("initialisedClasses", currentState.getInitialisedClasses().keySet().stream().map(MyClass::getFullName).toArray());

            var staticFields = currentState.getInitialisedClasses();

            jsonGenerator.writeObjectFieldStart("classes");

            for (var myCLass : staticFields.keySet()) {
                jsonGenerator.writeObjectField(myCLass.getFullName(), staticFields.get(myCLass));
            }
            jsonGenerator.writeEndObject();

//            jsonGenerator.writeObjectField("staticFields", currentState.getInitialisedClasses());
            jsonGenerator.writeObjectField("variablesArray", currentState.getVariablesArray());
            jsonGenerator.writeEndObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



