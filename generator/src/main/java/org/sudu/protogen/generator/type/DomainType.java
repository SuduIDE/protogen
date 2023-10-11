package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.Set;

public class DomainType extends TypeModel {

    public DomainType(TypeName typeName) {
        super(typeName);
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.builder()
                .add("$L.toGrpc()", expr)
                .build();
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.builder()
                .add("$T.fromGrpc($L)", getTypeName(), expr)
                .build();
    }
}
