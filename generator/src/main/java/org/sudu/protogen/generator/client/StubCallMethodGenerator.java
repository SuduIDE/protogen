package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.descriptors.RepeatedContainer;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.UnfoldedType;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static protogen.Options.IfNotFound.*;

public class StubCallMethodGenerator {

    protected final FieldSpec stubField;

    private final GenerationContext context;

    private final Method method;

    private final TypeModel returnType;

    public StubCallMethodGenerator(GenerationContext context, Method method, TypeModel returnType, FieldSpec stubField) {
        this.context = context;
        this.method = method;
        this.returnType = returnType;
        this.stubField = stubField;
    }

    public MethodSpec generate() {
        return MethodSpec.methodBuilder(method.generatedName() + "StubCall")
                .addModifiers(Modifier.PRIVATE)
                .addParameters(parameters())
                .addCode(new BodyGenerator().get())
                .returns(returnType.getTypeName())
                .addAnnotation(
                        method.ifNotFoundBehavior() == NULLIFY
                                ? context.configuration().nullableAnnotationClass()
                                : context.configuration().nonnullAnnotationClass()
                )
                .build();
    }

    private class BodyGenerator implements Supplier<CodeBlock> {

        @Override
        public CodeBlock get() {
            return new IfNotFoundDecorator(getStrategy()).get();
        }

        @NotNull
        private BodyGenerator getStrategy() {
            if (method.isOutputStreaming()) {
                return new StreamingBodyGenerator();
            } else {
                return new CommonBodyGenerator();
            }
        }

        private class CommonBodyGenerator extends BodyGenerator {
            @Override
            public CodeBlock get() {
                CodeBlock returnExpr = CodeBlock.of("$N.$L(request)", stubField, method.getName());
                if (returnType.getTypeName() != TypeName.VOID) {
                    returnExpr = CodeBlock.of("return $L", returnType.fromGrpcTransformer(returnExpr));
                }
                return returnExpr.toBuilder().add(";").build();
            }
        }

        private class StreamingBodyGenerator extends BodyGenerator {

            @Override
            public CodeBlock get() {
                if (!(returnType instanceof RepeatedType repType)) throw new IllegalArgumentException();
                // I write mapping here manually because input is always an Iterator<Grpc..> and output is specified by the RepeatedContainer option
                // So RepeatedType.fromGrpcTransformer is not suitable because it does only T<U> <--> T<V> mappings
                return CodeBlock.of("""
                                var iterator = $N.$L(request);
                                return $L
                                $>.map(i -> $L)$L;$<
                                """,
                        stubField,
                        method.getName(),
                        RepeatedContainer.ITERATOR.getToStreamExpr(CodeBlock.of("iterator")),
                        repType.getElementModel().fromGrpcTransformer(CodeBlock.of("i"), Set.of("i")),
                        repType.getRepeatedType().getCollectorExpr()
                );
            }
        }

        private class IfNotFoundDecorator extends BodyGenerator {

            private final BodyGenerator generator;

            public IfNotFoundDecorator(BodyGenerator generator) {
                this.generator = generator;
            }

            @Override
            public CodeBlock get() {
                if (method.ifNotFoundBehavior() == IGNORE) return generator.get();
                return CodeBlock.of("""
                                try {$>
                                $L
                                $<} catch ($T ex) {$>
                                if (ex.getStatus().getCode() == $T.NOT_FOUND.getCode()) {$>
                                $L
                                $<}
                                throw ex;
                                $<}
                                """,
                        generator.get(),
                        ClassName.get("io.grpc", "StatusRuntimeException"),
                        ClassName.get("io.grpc", "Status"),
                        ifNotFoundBehaviour()
                );
            }

            private CodeBlock ifNotFoundBehaviour() {
                if (method.ifNotFoundBehavior() == EMPTY) {
                    RepeatedContainer container = returnTypeContainer();
                    if (container == null) return CodeBlock.of("");
                    return CodeBlock.of("return $L;", container.getEmptyOne());
                }
                if (method.ifNotFoundBehavior() == NULLIFY) return CodeBlock.of("return null;");
                return CodeBlock.of("");
            }

            @Nullable
            private RepeatedContainer returnTypeContainer() {
                if (returnType instanceof RepeatedType rt)
                    return rt.getRepeatedType();
                if (returnType instanceof UnfoldedType ut && ut.getType() instanceof RepeatedType rt)
                    return rt.getRepeatedType();
                return null;
            }
        }
    }

    private List<ParameterSpec> parameters() {
        ClassName protoType = method.getInputType().getProtobufTypeName();
        return List.of(ParameterSpec.builder(protoType, "request").build());
    }
}
