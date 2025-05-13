package data;

public class AnalyzedMethod {
    String className;
    String methodName;
    String descriptor;
    boolean usesArguments = false;
    boolean usesEnvironment = false;
    boolean containsPutStatic = false;
    boolean containsGetStatic = false;

    AnalyzedMethod(String className, String methodName, String descriptor) {
        this.className = className;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    public void setUsesArguments() {
        usesArguments = true;
    }
    public void setUsesEnvironment() {
        usesEnvironment = true;
    }
    public void setContainsPutStatic() {
        containsPutStatic = true;
    }
    public void setContainsGetStatic() {
        containsGetStatic = true;
    }

   public boolean notContainsInteractionWithEnvironment() {
        return !usesEnvironment && !usesArguments && !containsGetStatic && !containsPutStatic;
    }

}
