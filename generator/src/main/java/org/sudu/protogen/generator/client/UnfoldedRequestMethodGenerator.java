package org.sudu.protogen.generator.client;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import org.sudu.protogen.descriptors.Method;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.message.ToGrpcMethodGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.utils.Poem;

import java.util.List;

public class UnfoldedRequestMethodGenerator extends MethodGeneratorBase {

    public UnfoldedRequestMethodGenerator(GenerationContext context, Method method, FieldSpec stubField) {
        super(context, method, stubField);
    }

    @Override
    protected List<ParameterSpec> parameters() {
        return method.getInputType().getFields().stream()
                .map(f -> new FieldGenerator(context, f).generate())
                .filter(FieldProcessingResult::isNonEmpty)
                .map(FieldProcessingResult::field)
                .map(Poem::fieldToParameter)
                .toList();
    }

    @Override
    protected CodeBlock body(TypeModel returnType, List<ParameterSpec> params) {
        ClassName protoType = method.getInputType().getProtobufTypeName();
        if (method.getInputType().isDomain()) {
            ClassName inputType = method.getInputType().getDomainTypeName(context.configuration().namingManager());
            CodeBlock paramsAsList = Poem.separatedSequence(
                    params.stream()
                            .map(p -> CodeBlock.builder().add("$N", p).build()) // extract param name
                            .toList(),
                    ",$W"
            );
            return CodeBlock.builder()
                    .addStatement("$T request = new $T($L).toGrpc()", protoType, inputType, paramsAsList)
                    .build();
        } else {
            List<FieldProcessingResult> processedFields = method.getInputType().getFields().stream()
                    .map(field -> new FieldGenerator(context, field).generate())
                    .filter(FieldProcessingResult::isNonEmpty)
                    .toList();
            CodeBlock builder = new ToGrpcMethodGenerator(context, protoType, processedFields, false).builder("requestBuilder");
            return CodeBlock.builder()
                    .add(builder)
                    .addStatement("$T request = requestBuilder.build()", protoType)
                    .build();
        }
    }

    @Override
    protected CodeBlock returnExpression(TypeModel returnType, List<ParameterSpec> params) {
        return CodeBlock.builder()
                .add("$L(request)", method.generatedName())
                .build();
    }
}
