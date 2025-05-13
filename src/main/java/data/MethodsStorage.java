package data;

import output.MyClass;
import output.MyMethod;

import java.util.*;

public class MethodsStorage {

    static int globalEpoch = 0;

    static Map<MyMethod, Integer> epochsMap = new HashMap<>();

    static Map<String, Map<String, Map<String, MyMethod>>> methodsMap = new HashMap<>();

    public static boolean contains(String className, String methodName, String descriptor) {
        if (!methodsMap.containsKey(className)) return false;
        Map<String, Map<String, MyMethod>> methodsInClass = methodsMap.get(className);
        if (!methodsInClass.containsKey(methodName)) return false;
        Map<String, MyMethod> methodsInMethod = methodsInClass.get(methodName);
        return methodsInMethod.containsKey(descriptor);
    }

    public static MyMethod getMethod(String className, String methodName, String descriptor) {
        if (!contains(className, methodName, descriptor)) return null;
        return methodsMap.get(className).get(methodName).get(descriptor);
    }

    public static void putMethod(String className, String methodName, String descriptor) {
        if (contains(className, methodName, descriptor)) return;
        Map<String, Map<String, MyMethod>> methodsInClass;
        if (methodsMap.containsKey(className)) {
            methodsInClass = methodsMap.get(className);
        }
        else {
            methodsInClass = new HashMap<>();
            methodsMap.put(className, methodsInClass);
        }
        Map<String, MyMethod> methodsInMethod;
        if (methodsInClass.containsKey(methodName)) {
            methodsInMethod = methodsInClass.get(methodName);
        }
        else {
            methodsInMethod = new HashMap<>();
            methodsInClass.put(methodName, methodsInMethod);
        }
        var method = new MyMethod(new MyClass(className), methodName, descriptor);
        methodsInMethod.put(descriptor, method);
        epochsMap.put(method, globalEpoch);
    }

    public static int getEpoch(String className, String methodName, String descriptor) {
        if (!contains(className, methodName, descriptor)) return 0;
        var method = getMethod(className, methodName, descriptor);
        if (method == null) return 0;
        return epochsMap.get(method);
    }

    public static void updateEpoch(String className, String methodName, String descriptor) {
        if (!contains(className, methodName, descriptor)) return;
        var method = getMethod(className, methodName, descriptor);
        if (method == null) return;
        epochsMap.put(method, globalEpoch);
    }

    public static int getGlobalEpoch() {
        return globalEpoch;
    }
    public static void increaseGlobalEpoch() {
        globalEpoch++;
    }

}
