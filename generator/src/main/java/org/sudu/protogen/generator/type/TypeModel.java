package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.sudu.protogen.utils.Name;

import java.util.Set;

public class TypeModel {

    private final Set<String> possibleDefinitions = Set.of("i", "$$i", "j", "$$j");

    private final TypeName typeName;

    public TypeModel(TypeName typeName) {
        this.typeName = typeName;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public final CodeBlock toGrpcTransformer(CodeBlock expr) {
        return toGrpcTransformer(expr, Set.of());
    }

    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return expr;
    }

    public final CodeBlock fromGrpcTransformer(CodeBlock expr) {
        return fromGrpcTransformer(expr, Set.of());
    }

    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return expr;
    }

    public String getterMethod(String protoFieldName) {
        return "get" + Name.toCamelCase(protoFieldName);
    }

    public String setterMethod(String protoFieldName) {
        return "set" + Name.toCamelCase(protoFieldName);
    }

    public boolean isPrimitive() {
        return false;
    }

    protected final String nextDefinition(Set<String> usedDefinitions) {
        for (String def : possibleDefinitions) {
            if (usedDefinitions.contains(def)) continue;
            return def;
        }
        throw new IllegalStateException("Failed to define a new variable!");
    }
}
