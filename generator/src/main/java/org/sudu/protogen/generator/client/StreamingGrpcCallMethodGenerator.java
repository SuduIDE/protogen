package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.apache.commons.lang3.Validate;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.type.IteratorType;
import org.sudu.protogen.generator.type.RepeatedType;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.protobuf.Method;
import org.sudu.protogen.utils.Name;

import javax.lang.model.element.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class StreamingGrpcCallMethodGenerator extends MethodGeneratorBase {

    private final GrpcCallMethodGenerator commonGen;

    public StreamingGrpcCallMethodGenerator(GenerationContext context, Method method, FieldSpec stubField) {
        super(context, method, stubField);
        Validate.validState(method.isOutputStreaming());
        this.commonGen = new GrpcCallMethodGenerator(context, method, stubField);
    }

    @Override
    protected List<ParameterSpec> parameters() {
        return commonGen.parameters();
    }

    @Override
    protected CodeBlock body(TypeModel returnType, List<ParameterSpec> params) {
        if (returnType.getTypeName().toString().equalsIgnoreCase("void")) {
            return CodeBlock.of("");
        }
        CodeBlock call = commonGen.onlyCallReturnExpression(returnType, params);
        if (method.isStreamingToList()) {
            Validate.validState(returnType instanceof RepeatedType);
            RepeatedType repType = (RepeatedType) returnType;
            return CodeBlock.builder()
                    .addStatement("var response = $L", call)
                    .addStatement(new IteratorWrapper().wrapToIterable("response", repType.getElementModel()))
                    .build();
        } else {
            return CodeBlock.builder()
                    .addStatement("var response = $L", call)
                    .build();
        }
    }

    @Override
    protected CodeBlock returnExpression(TypeModel returnType, List<ParameterSpec> params) {
        if (returnType.getTypeName().toString().equalsIgnoreCase("void")) {
            return commonGen.onlyCallReturnExpression(returnType, params);
        }
        if (method.isStreamingToList()) {
            return CodeBlock.of("$T.stream(iterable.spliterator(), false).toList()", StreamSupport.class);
        } else {
            Validate.validState(returnType instanceof IteratorType);
            IteratorType itType = (IteratorType) returnType;
            return new IteratorWrapper().wrapIterator("response", itType.getIteratedType());
        }
    }

    private class IteratorWrapper {

        public CodeBlock wrapToIterable(String outerIteratorIdentifier, TypeModel domainType) {
            return CodeBlock.builder()
                    .add(
                            "$T iterable = () -> $L",
                            ParameterizedTypeName.get(ClassName.get(Iterable.class), domainType.getTypeName()),
                            wrapIterator("response", domainType)
                    )
                    .build();
        }

        public CodeBlock wrapIterator(String outerIteratorIdentifier, TypeModel domainType) {
            return CodeBlock.of(
                    "$L",
                    TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Iterator.class), domainType.getTypeName()))
                            .addMethod(hasNext(outerIteratorIdentifier))
                            .addMethod(next(outerIteratorIdentifier, domainType))
                            .build()
            );
        }

        private MethodSpec hasNext(String outerIteratorIdentifier) {
            return MethodSpec.methodBuilder("hasNext")
                    .returns(TypeName.BOOLEAN)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return $L.hasNext()", outerIteratorIdentifier)
                    .build();
        }

        private MethodSpec next(String outerIteratorIdentifier, TypeModel domainType) {
            CodeBlock next = CodeBlock.of("$L.next()", outerIteratorIdentifier);
            if (method.doUnfoldResponse()) {
                next = next.toBuilder().add(".get$L()", Name.toCamelCase(method.unfoldedResponseField().getName())).build();
            }
            return MethodSpec.methodBuilder("next")
                    .returns(domainType.getTypeName())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return $L", domainType.fromGrpcTransformer(next))
                    .build();
        }
    }
}
