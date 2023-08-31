package org.sudu.protogen.test;

import org.sudu.protogen.test.unfolding.UnfoldingTest;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

public class TestUtils {

    private final String pkg;

    public TestUtils(String pkg) {
        this.pkg = pkg;
    }

    public Class<?> loadDomain(String name) throws AssertionError {
        try {
            return UnfoldingTest.class.getClassLoader().loadClass("org.sudu.protogen.test." + pkg + "." + name);
        } catch (ClassNotFoundException e) {
            throw new DomainNotFoundError(name + " was not found", e);
        }
    }

    public RecordComponent getComponentByName(Class<?> clazz, String name) throws AssertionError {
        return Arrays.stream(clazz.getRecordComponents())
                .filter(c -> c.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new ComponentNotFoundError("Component %s was not found".formatted(name)));
    }

    public static class DomainNotFoundError extends AssertionError {
        public DomainNotFoundError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ComponentNotFoundError extends AssertionError {
        public ComponentNotFoundError(String message) {
            super(message);
        }
    }
}
