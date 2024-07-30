package output;

import java.util.Objects;

public class MyMethod {
    MyClass myClass;
    String methodName;
    String methodDescriptor;

    public MyMethod(MyClass myClass, String methodName, String methodDescriptor) {
        this.myClass = myClass;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyMethod myMethod = (MyMethod) o;
        return myClass.equals(myMethod.myClass) && Objects.equals(methodName, myMethod.methodName) && Objects.equals(methodDescriptor, myMethod.methodDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myClass, methodName, methodDescriptor);
    }
}
