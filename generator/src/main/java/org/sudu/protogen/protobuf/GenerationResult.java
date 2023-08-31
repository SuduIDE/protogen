package org.sudu.protogen.protobuf;

import java.util.List;

public record GenerationResult(List<File> generatedFiles) {

    public record File(String packageName, String fileName, String content) {
    }
}
