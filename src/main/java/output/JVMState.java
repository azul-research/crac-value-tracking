package output;

import entitites.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JVMState {

    private final Set<MyClass> loadedClasses;
    private Map<MyClass, ClassStaticFields> initializedClasses;

    public JVMState() {
        this(new HashSet<>(), new HashMap<>());
    }

    public JVMState(Set<MyClass> loadedClasses, Map<MyClass, ClassStaticFields> initializedClasses) {
        this.loadedClasses = new HashSet<>(loadedClasses);
        setInitializedClasses(initializedClasses);
    }

    public JVMState(JVMState state) {
        this.loadedClasses = new HashSet<>(state.loadedClasses);
        setInitializedClasses(state.initializedClasses);
    }

    public Set<MyClass> getLoadedClasses() {
        return loadedClasses;
    }

    public Map<MyClass, ClassStaticFields> getInitializedClasses() {
        return initializedClasses;
    }

    public Entity getStaticField(MyClass myClass, String field) {
        return initializedClasses.get(myClass).getField(field);
    }

    public void setInitializedClasses(Map<MyClass, ClassStaticFields> newInitializedClasses) {
        this.initializedClasses = new HashMap<>();

        for (var myClass : newInitializedClasses.keySet()) {
            var staticFields = newInitializedClasses.get(myClass);
            this.initializedClasses.put(myClass, new ClassStaticFields(myClass, staticFields.getStaticFields()));
        }
    }

    public boolean containsLoadedClass(MyClass myClass) {
        return loadedClasses.contains(myClass);
    }

    public boolean containsInitializedClass(MyClass myClass) {
        return initializedClasses.containsKey(myClass);
    }

    public void addLoadedClass(MyClass myClass) {
        loadedClasses.add(myClass);
    }

    public void addLoadedClasses(Set<MyClass> myClasses) {
        loadedClasses.addAll(myClasses);
    }

    public void addInitialisedClass(MyClass myClass, ClassStaticFields staticFields) {
        initializedClasses.put(myClass, staticFields);
    }

    public void addInitialisedClasses(Map<MyClass, ClassStaticFields> myClasses) {
        initializedClasses.putAll(myClasses);
    }

    public void addClassStaticField(MyClass myClass, String fieldName, Entity field) {
        initializedClasses.get(myClass).addStaticField(fieldName, field);
    }

}
