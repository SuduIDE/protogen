package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.Set;

public class VoidType extends TypeModel {

    public VoidType() {
        super(TypeName.VOID);
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.of("null");
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.of("(Void)null");
    }
}
