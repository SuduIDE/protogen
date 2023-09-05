package org.sudu.protogen.descriptors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import protogen.Options;

import java.util.stream.Collectors;

public enum RepeatedContainer {
    LIST(ClassName.get("java.util", "List"), CodeBlock.of("$T.toList()", Collectors.class)),
    SET(ClassName.get("java.util", "Set"), CodeBlock.of("$T.toSet()", Collectors.class));

    private final ClassName typeName;
    private final CodeBlock collectorExpr;

    RepeatedContainer(ClassName typeName, CodeBlock collectorExpr) {
        this.typeName = typeName;
        this.collectorExpr = collectorExpr;
    }

    public static RepeatedContainer fromGrpc(Options.RepeatedContainer proto) {
        return switch (proto) {
            case UNRECOGNIZED -> throw new IllegalArgumentException();
            case LIST -> RepeatedContainer.LIST;
            case SET -> RepeatedContainer.SET;
        };
    }

    public ClassName getTypeName() {
        return typeName;
    }

    public CodeBlock getCollectorExpr() {
        return collectorExpr;
    }
}
