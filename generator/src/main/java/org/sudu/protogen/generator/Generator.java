package org.sudu.protogen.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.Enum;
import org.sudu.protogen.descriptors.*;
import org.sudu.protogen.generator.client.ClientGenerator;
import org.sudu.protogen.generator.enumeration.EnumGenerator;
import org.sudu.protogen.generator.message.MessageGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Generator {

    private final GenerationContext context;

    public Generator(@NotNull GenerationContext context) {
        this.context = context;
    }

    @NotNull
    public GenerationResult generate() {
        List<GenerationResult.File> generatedFiles = generateFiles().stream()
                .map(this::javaFileToResult)
                .toList();
        return new GenerationResult(generatedFiles);
    }

    private GenerationResult.File javaFileToResult(JavaFile gen) {
        StringBuilder content = new StringBuilder();
        try {
            gen.writeTo(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new GenerationResult.File(gen.packageName, gen.typeSpec.name, content.toString());
    }

    private List<JavaFile> generateFiles() {
        List<JavaFile> result = new ArrayList<>();

        for (File file : context.filesToGenerate()) {

            String packageName = file.getGeneratePackage();
            for (EnumOrMessage type : file.getNested()) {
                if (!type.doGenerate()) continue;
                generateType(type, false)
                        .map(typeSpec -> JavaFile.builder(packageName, typeSpec)
                                .indent(getIndentation())
                                .build()
                        )
                        .forEach(result::add);
            }
        }

        for (File file : context.filesToGenerate()) {
            String packageName = file.getGeneratePackage();
            for (Service service : file.getServices()) {
                if (!service.doGenerate()) {
                    continue;
                }
                TypeSpec clientTypeSpec = new ClientGenerator(context, service).generate();
                result.add(JavaFile.builder(packageName, clientTypeSpec)
                        .indent(getIndentation())
                        .build());
            }
        }

        return result;
    }

    private Stream<TypeSpec> generateType(EnumOrMessage descriptor, boolean forceGenerate) {
        Stream<TypeSpec> result = Stream.of();
        if (!descriptor.doGenerate() && !forceGenerate) return result;
        for (EnumOrMessage nested : descriptor.getNested()) {
            result = Stream.concat(result, generateType(nested, false));
        }
        TypeSpec generated = invokeTypeGenerator(descriptor);
        context.domains().put(descriptor, generated);
        result = Stream.concat(result, Stream.of(generated));
        return result;
    }

    @NotNull
    private TypeSpec invokeTypeGenerator(EnumOrMessage type) {
        if (type instanceof Message msg)
            return new MessageGenerator(context, msg).generate();
        else if (type instanceof Enum en)
            return new EnumGenerator(context, en).generate();
        throw new IllegalStateException();
    }

    private String getIndentation() {
        return StringUtils.repeat(" ", context.configuration().indentationSize());
    }
}
