package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Message;

import java.util.Set;

public class VoidType extends TypeModel {

    @Nullable
    private final Message emptyMessage;

    public VoidType(@Nullable Message emptyMessage) {
        super(TypeName.VOID);
        this.emptyMessage = emptyMessage;
    }

    public VoidType() {
        this(null);
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        if (emptyMessage != null) {
            return CodeBlock.of("$T.newBuilder().build()", emptyMessage.getProtobufTypeName());
        }
        return CodeBlock.of("null");
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return CodeBlock.of("(Void)null");
    }
}
