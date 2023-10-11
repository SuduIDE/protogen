package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang3.Validate;
import org.sudu.protogen.descriptors.Message;

import java.util.Set;

public class UnfoldedType extends TypeModel {

    private final TypeModel type;

    private final String unfoldedFieldName;

    private final TypeName unfoldedTypeName;

    public UnfoldedType(TypeModel type, Message originalMessage) {
        super(type.getTypeName());
        Validate.validState(originalMessage.getFields().size() == 1);
        this.type = type;
        this.unfoldedFieldName = originalMessage.getFields().get(0).getName();
        this.unfoldedTypeName = originalMessage.getProtobufTypeName();
    }

    public TypeModel getType() {
        return type;
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return type.fromGrpcTransformer(CodeBlock.of("$L.$L()", expr, type.getterMethod(unfoldedFieldName)), usedDefinitions);
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.of("$T.newBuilder().$L($L).build()", unfoldedTypeName, type.setterMethod(unfoldedFieldName), type.toGrpcTransformer(expr, usedDefinitions));
    }

    @Override
    public boolean isPrimitive() {
        return type.isPrimitive();
    }
}
