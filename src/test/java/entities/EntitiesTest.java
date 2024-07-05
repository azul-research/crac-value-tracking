package entities;

import entitites.derivatives.RootDerivative;
import entitites.derivatives.StraightDerivative;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EntitiesTest {

    @Test
    public void straightDerInfoTest() {
        String name = "args";
        RootDerivative root = new RootDerivative(name);
        StraightDerivative straightDerivative = new StraightDerivative(10, root);
        String expected = "assigned to derivative on line 10:\n" +
                          "\troot derivative " + name;

        assertEquals(expected, straightDerivative.info(0));
    }

    @Test
    public void rootDerInfoTest() {
        String name = "args";
        RootDerivative root = new RootDerivative(name);
        String expected = "root derivative " + name;

        assertEquals(expected, root.info(0));
    }
}
