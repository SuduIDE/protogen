package org.sudu.protogen.generator;

import org.sudu.protogen.descriptors.File;

import java.util.List;

public record GenerationRequest(
        List<? extends File> allFiles,
        List<String> filesToGenerateNames
) {
}
