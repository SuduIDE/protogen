package org.sudu.protogen.protobuf;

import java.util.List;

public record GenerationRequest(
        List<? extends File> allFiles,
        List<String> filesToGenerateNames
) {
}
