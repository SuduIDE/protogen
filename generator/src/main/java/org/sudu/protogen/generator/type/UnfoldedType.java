package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.Validate;
import org.sudu.protogen.descriptors.Message;

public class UnfoldedType extends TypeModel {

    private final TypeModel type;

    private final String unfoldedFieldName;

    private final TypeName unfoldedTypeName;

    public UnfoldedType(TypeModel type, Message unfoldedType) {
        super(type.getTypeName());
        Validate.validState(unfoldedType.getFields().size() == 1);
        this.type = type;
        this.unfoldedFieldName = unfoldedType.getFields().get(0).getName();
        this.unfoldedTypeName = unfoldedType.getProtobufTypeName();
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        return CodeBlock.of("$L.$L()", type.fromGrpcTransformer(expr), type.getterMethod(unfoldedFieldName));
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        return CodeBlock.of("$T.newBuilder().$L($L).build()", unfoldedTypeName, type.setterMethod(unfoldedFieldName), type.toGrpcTransformer(expr));
    }

    @Override
    public boolean isPrimitive() {
        return type.isPrimitive();
    }
}
