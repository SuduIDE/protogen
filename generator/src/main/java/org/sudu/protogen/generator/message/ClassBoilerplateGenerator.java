package org.sudu.protogen.generator.message;

import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.utils.Name;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassBoilerplateGenerator {

    private final ClassName generatedType;

    private final List<FieldSpec> fields;

    public ClassBoilerplateGenerator(@NotNull ClassName generatedType, @NotNull List<FieldSpec> fields) {
        this.generatedType = generatedType;
        this.fields = fields;
    }

    @NotNull
    public List<MethodSpec> generateBoilerplate() {
        return Stream.concat(generateGetters(), Stream.of(
                generateConstructor(),
                generateEquals(),
                generateHashCode()
        )).toList();
    }

    @NotNull
    public MethodSpec generateConstructor() {
        CodeBlock.Builder assignments = CodeBlock.builder();
        for (FieldSpec field : fields) {
            assignments.addStatement("this.$L = $L", field.name, field.name);
        }
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameters(fields.stream().map(Poem::fieldToParameter).toList())
                .addCode(assignments.build())
                .build();
    }

    @NotNull
    public MethodSpec generateHashCode() {
        String fieldsList = fields.stream()
                .map(field -> field.name)
                .collect(Collectors.joining(", "));
        return MethodSpec.methodBuilder("hashCode")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT)
                .addAnnotation(Override.class)
                .addStatement(CodeBlock.builder()
                        .add("return $T.hash($L)", ClassName.get(Objects.class), fieldsList)
                        .build()
                )
                .build();
    }

    @NotNull
    public MethodSpec generateEquals() {
        String name = generatedType.simpleName();
        CodeBlock.Builder comparisons = CodeBlock.builder();
        var fieldIt = fields.iterator();
        while (fieldIt.hasNext()) {
            FieldSpec field = fieldIt.next();
            comparisons.add("$T.equals($L, o$$.$L)", ClassName.get(Objects.class), field.name, field.name);
            if (fieldIt.hasNext()) comparisons.add(" && ");
        }

        return MethodSpec.methodBuilder("equals")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(TypeName.OBJECT, "o").build())
                .returns(TypeName.BOOLEAN)
                .addAnnotation(Override.class)
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addStatement(name + " o$$ = (" + name + ") o")
                .addStatement(CodeBlock.builder().add("return ").add(comparisons.build()).build())
                .build();
    }

    @NotNull
    public Stream<MethodSpec> generateGetters() {
        return fields.stream().map(
                field -> MethodSpec.methodBuilder("get" + Name.toCamelCase(field.name))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(field.type)
                        .addAnnotations(field.annotations)
                        .addStatement("return $L", field.name)
                        .build()
        );
    }
}
