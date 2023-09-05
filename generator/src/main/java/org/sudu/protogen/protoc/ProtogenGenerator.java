package org.sudu.protogen.protoc;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.Protogen;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.GenerationResult;
import org.sudu.protogen.protoc.plugin.Generator;
import org.sudu.protogen.protoc.plugin.GeneratorException;

import java.util.List;
import java.util.stream.Stream;

public class ProtogenGenerator extends Generator {

    @Override
    public List<CodeGeneratorResponse.File> generateFiles(CodeGeneratorRequest request) throws GeneratorException {
        var configuration = Configuration.DEFAULT;
        return new Protogen(RequestBuilder.fromProtocRequest(request), configuration)
                .generate()
                .generatedFiles()
                .stream()
                .map(this::buildFile)
                .toList();
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
