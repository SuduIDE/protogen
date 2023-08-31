package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.sudu.protogen.utils.Name;

public class TypeModel {

    private final TypeName typeName;

    public TypeModel(TypeName typeName) {
        this.typeName = typeName;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        return expr;
    }

    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
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
}
