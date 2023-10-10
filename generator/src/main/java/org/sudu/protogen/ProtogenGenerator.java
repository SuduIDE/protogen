package org.sudu.protogen;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.config.YamlExternalConfigurationParser;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.GenerationRequest;
import org.sudu.protogen.generator.GenerationResult;
import org.sudu.protogen.plugin.Generator;
import org.sudu.protogen.plugin.GeneratorException;

import java.util.List;
import java.util.stream.Stream;

public class ProtogenGenerator extends Generator {

    @Override
    public List<CodeGeneratorResponse.File> generateFiles(CodeGeneratorRequest request) throws GeneratorException {
        String requestParam = request.getParameter();
        Configuration configuration = Configuration.builder().build();
        if (!requestParam.isBlank()) {
            String configFilePath = requestParam.replace("config=", "").replace("*", ":");
            configuration = new YamlExternalConfigurationParser(configFilePath).parse();
        }
        return generate(RequestBuilder.fromProtocRequest(request, configuration))
                .generatedFiles()
                .stream()
                .map(this::buildFile)
                .toList();
    }

    private GenerationResult generate(GenerationRequest request) {
        var allFiles = request.allFiles();
        var filesToGenerate = allFiles.stream()
                .filter(file -> request.filesToGenerateNames().contains(file.getName()))
                .toList();
        var context = new GenerationContext(request.configuration());
        return new org.sudu.protogen.generator.Generator(context, filesToGenerate).generate();
    }

    @NotNull
    private CodeGeneratorResponse.File buildFile(GenerationResult.File generatedFile) {
        return makeFile(
                buildJavaClassName(generatedFile.packageName(), generatedFile.fileName()),
                generatedFile.content()
        );
    }

    @NotNull
    private String buildJavaClassName(@NotNull String packageName, @NotNull String fileName) {
        String dir = packageName.replace('.', '/');
        if (StringUtils.isEmpty(dir)) {
            return fileName + ".java";
        } else {
            return dir + "/" + fileName + ".java";
        }
    }

    @Override
    protected List<CodeGeneratorResponse.Feature> supportedFeatures() {
        return Stream.concat(
                super.supportedFeatures().stream(),
                Stream.of(CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL)
        ).toList();
    }

    @SuppressWarnings("rawtypes")
    public List<GeneratedExtension> getExtensions() {
        return Options.getOptionsExtensions();
    }
}
