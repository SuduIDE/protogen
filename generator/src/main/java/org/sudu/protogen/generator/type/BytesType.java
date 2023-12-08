package org.sudu.protogen.generator.type;

import com.google.protobuf.ByteString;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.Set;

public class BytesType extends TypeModel {

    public BytesType() {
        super(ArrayTypeName.of(TypeName.BYTE));
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.of("$T.copyFrom($L)", ByteString.class, expr);
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.of("$L.toByteArray()", expr);
    }
}
