package output;

import java.util.Objects;

public class MyClass {
    private final String fullName;


    public MyClass(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyClass myClass = (MyClass) o;
        return Objects.equals(fullName, myClass.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fullName);
    }
}
