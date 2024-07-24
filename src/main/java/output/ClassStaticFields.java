package output;

import entitites.Entity;

import java.util.HashMap;
import java.util.Map;

public class ClassStaticFields {

    private MyClass myClass;

    private final Map<String, Entity> staticFields;

     public Entity getField(String name) {
         return staticFields.get(name);
     }

     public MyClass getMyClass() {
         return myClass;
     }

     public Map<String, Entity> getStaticFields() {
         return staticFields;
     }

     public void addStaticField(String fieldName, Entity field) {
         staticFields.put(fieldName, field);
     }

     public ClassStaticFields(MyClass myClass) {
         this(myClass, new HashMap<>());

     }

     public ClassStaticFields(MyClass myClass, Map<String, Entity> staticFields) {
         this.myClass = myClass;
         this.staticFields = new HashMap<>(staticFields);
     }


}
