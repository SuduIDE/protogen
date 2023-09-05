package org.sudu.protogen.test.comparatorRef;

import java.util.Comparator;

public class TestComparators {

    public static final Comparator<Person> PERSON_COMPARATOR = Comparator.comparing(Person::name).thenComparing(Person::age);
}
