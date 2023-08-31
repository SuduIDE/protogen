package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.sudu.protogen.utils.Name;

public class UnfoldedType extends TypeModel {

    private final TypeModel type;

    private final String unfoldedFieldName;

    private final TypeName unfoldedTypeName;

    public UnfoldedType(TypeModel type, String unfoldedFieldName, TypeName unfoldedTypeName) {
        super(type.getTypeName());
        this.type = type;
        this.unfoldedFieldName = Name.toCamelCase(unfoldedFieldName);
        this.unfoldedTypeName = unfoldedTypeName;
    }

    public TypeModel getType() {
        return type;
    }

    public String getUnfoldedFieldName() {
        return unfoldedFieldName;
    }

    public TypeName getUnfoldedTypeName() {
        return unfoldedTypeName;
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        return CodeBlock.of("$L.$L()", expr, type.getterMethod(unfoldedFieldName));
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        return CodeBlock.of("$T.newBuilder().$L($L).build()", unfoldedTypeName, type.setterMethod(unfoldedFieldName), expr);
    }

    @Override
    public boolean isPrimitive() {
        return type.isPrimitive();
    }
}
