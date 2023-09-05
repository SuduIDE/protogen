package org.sudu.protogen.descriptors;

import java.util.List;

public record GenerationRequest(
        List<? extends File> allFiles,
        List<String> filesToGenerateNames
) {
}
