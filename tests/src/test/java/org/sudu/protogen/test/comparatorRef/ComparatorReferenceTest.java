package org.sudu.protogen.test.comparatorRef;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComparatorReferenceTest {

    @Test
    public void Person_should_compare_correctly() {
        Person anton20 = new Person("Anton", 20);
        Person anton25 = new Person("Anton", 25);
        Person lena20 = new Person("Lena", 20);

        Assertions.assertTrue(anton20.compareTo(anton25) < 0);
        Assertions.assertTrue(anton20.compareTo(anton20) == 0);
        Assertions.assertTrue(lena20.compareTo(anton25) > 0);
        Assertions.assertTrue(lena20.compareTo(anton20) > 0);
        Assertions.assertTrue(anton20.compareTo(lena20) < 0);
    }
}
