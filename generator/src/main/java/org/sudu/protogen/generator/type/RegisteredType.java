package org.sudu.protogen.generator.type;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.sudu.protogen.config.RegisteredTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RegisteredType extends TypeModel {

    private final TypeName protoType;

    private final RegisteredTransformer registration;

    public RegisteredType(TypeName typeName, TypeName protoType, RegisteredTransformer registration) {
        super(typeName);
        this.protoType = protoType;
        this.registration = registration;
    }

    private CodeBlock fromRule(RegisteredTransformer.TransformRule transformRule, CodeBlock l, TypeName t) {
        // The regexp is to split keeping delimiters
        // See https://stackoverflow.com/questions/2206378/how-to-split-a-string-but-also-keep-the-delimiters
        List<String> tokens = Arrays.stream(transformRule.rule().split("((?<=\\$L)|(?=\\$L))"))
                .flatMap(token -> Arrays.stream(token.split("((?<=\\$T)|(?=\\$T))")))
                .flatMap(token -> Arrays.stream(token.split("((?<=\\$t)|(?=\\$t))")))
                .toList();
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        int paramIt = 0;
        for (String token : tokens) {
            switch (token) {
                case "$L" -> codeBuilder.add("$L", l);
                case "$T" -> codeBuilder.add("$T", t);
                case "$t" -> codeBuilder.add("$T", transformRule.params()[paramIt++]);
                default -> codeBuilder.add(token);
            }
        }
        return codeBuilder.build();
    }

    @Override
    public CodeBlock toGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        CodeBlock setter = fromRule(registration.javaToProto(), expr, getTypeName());
        return CodeBlock.builder()
                .add("$T.newBuilder()", protoType)
                .add(setter)
                .add(".build()")
                .build();
    }

    @Override
    public CodeBlock fromGrpcTransformer(CodeBlock expr, Set<String> usedDefinitions) {
        return fromRule(registration.protoToJava(), expr, getTypeName());
    }
}
