package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.sudu.protogen.config.RegisteredTransformer;

import java.util.Arrays;

public class RegisteredType extends TypeModel {

    private final TypeName protoType;

    private final RegisteredTransformer registration;

    public RegisteredType(TypeName typeName, TypeName protoType, RegisteredTransformer registration) {
        super(typeName);
        this.protoType = protoType;
        this.registration = registration;
    }

    private CodeBlock fromRule(RegisteredTransformer.TransformRule transformRule, CodeBlock l, TypeName t) {
        var tokens = Arrays.stream(transformRule.rule().split("\\$L")).iterator();
        var builder = CodeBlock.builder();
        int paramIt = 0;
        while (tokens.hasNext()) {
            var typedTokens = Arrays.stream(tokens.next().split("\\$T")).iterator();
            while (typedTokens.hasNext()) {
                var parametrizedTokens = Arrays.stream(typedTokens.next().split("\\$t")).iterator();
                while (parametrizedTokens.hasNext()) {
                    builder.add(parametrizedTokens.next());
                    if (parametrizedTokens.hasNext()) builder.add("$T", transformRule.params()[paramIt++]);
                }
                if (typedTokens.hasNext()) builder.add("$T", t);
            }
            if (tokens.hasNext()) builder.add("$L", l);
        }
        if (transformRule.rule().endsWith("$L")) builder.add("$L", l);
        return builder.build();
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr) {
        CodeBlock setter = fromRule(registration.javaToProto(), expr, getTypeName());
        return CodeBlock.builder()
                .add("$T.newBuilder()", protoType)
                .add(setter)
                .add(".build()")
                .build();
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr) {
        return fromRule(registration.protoToJava(), expr, getTypeName());
    }
}
