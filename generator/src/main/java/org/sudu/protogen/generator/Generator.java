package org.sudu.protogen.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.File;
import org.sudu.protogen.descriptors.Service;
import org.sudu.protogen.generator.client.ClientGenerator;
import org.sudu.protogen.generator.server.BaseServiceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    private final GenerationContext context;

    private final List<? extends File> filesToGenerate;

    public Generator(@NotNull GenerationContext context, List<? extends File> filesToGenerate) {
        this.context = context;
        this.filesToGenerate = filesToGenerate;
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

        for (File file : filesToGenerate) {
            String packageName = file.getGeneratePackage();
            for (EnumOrMessage type : file.getNested()) {
                if (!type.doGenerate()) continue;
                result.add(JavaFile.builder(packageName, new EnumOrMessageGenerator(context, type).generate())
                        .indent(getIndentation())
                        .build()
                );
            }
            for (Service service : file.getServices()) {
                if (!service.doGenerate()) {
                    continue;
                }
                TypeSpec clientTypeSpec = new ClientGenerator(context, service).generate();
                result.add(JavaFile.builder(packageName, clientTypeSpec)
                        .indent(getIndentation())
                        .build());
                TypeSpec serviceTypeSpec = new BaseServiceGenerator(context, service).generate();
                result.add(JavaFile.builder(packageName, serviceTypeSpec)
                        .indent(getIndentation())
                        .build());
            }
        }
        return result;
    }

    private String getIndentation() {
        return StringUtils.repeat(" ", context.configuration().indentationSize());
    }
}
