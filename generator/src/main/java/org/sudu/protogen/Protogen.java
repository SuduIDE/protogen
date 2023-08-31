package org.sudu.protogen;

import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.Generator;
import org.sudu.protogen.generator.TypeTable;
import org.sudu.protogen.generator.field.processors.FieldTypeProcessor;
import org.sudu.protogen.generator.type.processors.TypeProcessor;
import org.sudu.protogen.protobuf.GenerationRequest;
import org.sudu.protogen.protobuf.GenerationResult;

import java.util.HashMap;

public class Protogen {

    private final GenerationRequest request;

    private final Configuration configuration;

    public Protogen(@NotNull GenerationRequest request, @NotNull Configuration configuration) {
        this.request = request;
        this.configuration = configuration;
    }

    public GenerationResult generate() {
        var allFiles = request.allFiles();
        var filesToGenerate = allFiles.stream()
                .filter(file -> request.filesToGenerateNames().contains(file.getName()))
                .toList();
        var context = new GenerationContext(
                filesToGenerate,
                configuration,
                TypeProcessor.Chain.getProcessingChain(),
                FieldTypeProcessor.Chain.getProcessingChain(),
                TypeTable.makeProtoTypeTable(allFiles, configuration),
                TypeTable.makeDomainTypeTable(allFiles, configuration),
                new HashMap<>(),
                new HashMap<>()
        );
        return new Generator(context).generate();
    }
}
