package org.sudu.protogen.protoc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import org.sudu.protogen.descriptors.File;
import org.sudu.protogen.descriptors.GenerationRequest;
import org.sudu.protogen.protoc.plugin.GeneratorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBuilder {

    public static GenerationRequest fromProtocRequest(PluginProtos.CodeGeneratorRequest protocRequest) {
        var extractedFiles = extractFileDescriptors(protocRequest.getProtoFileList());
        List<File> allFiles = extractedFiles.stream()
                .map(File::new)
                .toList();
        List<String> filesToGenerate = protocRequest.getFileToGenerateList().stream().toList();
        return new GenerationRequest(allFiles, filesToGenerate);
    }

    private static List<Descriptors.FileDescriptor> extractFileDescriptors(List<DescriptorProtos.FileDescriptorProto> fileDescriptorProtoList) {
        Map<String, Descriptors.FileDescriptor> filesByName = new HashMap<>();
        for (DescriptorProtos.FileDescriptorProto fp : fileDescriptorProtoList) {
            try {
                Descriptors.FileDescriptor[] dependencies = fp.getDependencyList().stream()
                        .map(filesByName::get)
                        .toArray(Descriptors.FileDescriptor[]::new);

                var fd = Descriptors.FileDescriptor.buildFrom(fp, dependencies);
                filesByName.put(fp.getName(), fd);
            } catch (Descriptors.DescriptorValidationException e) {
                throw new GeneratorException(e.getMessage());
            }
        }
        return new ArrayList<>(filesByName.values());
    }
}
