package org.sudu.protogen.descriptors;

import java.util.List;

public record GenerationResult(List<File> generatedFiles) {

    public record File(String packageName, String fileName, String content) {
    }
}
