package data;

import output.MyClass;
import output.MyMethod;

import java.util.HashMap;
import java.util.Map;

public class AnalyzedMethodsStorage {
    static Map<String, Map<String, Map<String, AnalyzedMethod>>> methodsMap = new HashMap<>();

    public static boolean contains(String className, String methodName, String descriptor) {
        if (!methodsMap.containsKey(className)) return false;
        Map<String, Map<String, AnalyzedMethod>> methodsInClass = methodsMap.get(className);
        if (!methodsInClass.containsKey(methodName)) return false;
        Map<String, AnalyzedMethod> methodsInMethod = methodsInClass.get(methodName);
        return methodsInMethod.containsKey(descriptor);
    }

    public static AnalyzedMethod getMethod(String className, String methodName, String descriptor) {
        if (!contains(className, methodName, descriptor)) return null;
        return methodsMap.get(className).get(methodName).get(descriptor);
    }
    
    public static AnalyzedMethod putMethod(String className, String methodName, String descriptor) {
        if (contains(className, methodName, descriptor)) return getMethod(className, methodName, descriptor);
        Map<String, Map<String, AnalyzedMethod>> methodsInClass;
        if (methodsMap.containsKey(className)) {
            methodsInClass = methodsMap.get(className);
        }
        else {
            methodsInClass = new HashMap<>();
            methodsMap.put(className, methodsInClass);
        }
        Map<String, AnalyzedMethod> methodsInMethod;
        if (methodsInClass.containsKey(methodName)) {
            methodsInMethod = methodsInClass.get(methodName);
        }
        else {
            methodsInMethod = new HashMap<>();
            methodsInClass.put(methodName, methodsInMethod);
        }
        var method = new AnalyzedMethod(className, methodName, descriptor);
        methodsInMethod.put(descriptor, method);
        return method;
    }
}
