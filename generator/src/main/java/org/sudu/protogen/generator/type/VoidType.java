package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public class VoidType extends TypeModel {

    public VoidType() {
        super(TypeName.VOID);
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        return CodeBlock.of("null");
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        return CodeBlock.of("(Void)null");
    }
}
