/*
 *  Copyright (c) 2019, Salesforce.com, Inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package org.sudu.protogen.plugin;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.compiler.PluginProtos;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ProtocPlugin is the main entry point for running one or more java-base protoc plugins. This class handles
 * I/O marshaling and error reporting.
 */
public final class ProtocPlugin {

    private ProtocPlugin() {
    }

    /**
     * Apply a single generator to the parsed proto descriptor.
     *
     * @param generator The generator to run.
     */
    public static void generate(@NotNull Generator generator) {
        Validate.notNull(generator, "generator");
        generate(Collections.singletonList(generator));
    }

    /**
     * Apply multiple generators to the parsed proto descriptor, aggregating their results.
     *
     * @param generators The list of generators to run.
     */
    public static void generate(@NotNull List<Generator> generators) {
        generate(generators, Collections.emptyList());
    }

    /**
     * Apply multiple generators to the parsed proto descriptor, aggregating their results.
     * Also register the given extensions so they may be processed by the generator.
     *
     * @param generators The list of generators to run.
     * @param extensions The list of extensions to register.
     */
    public static void generate(
            @NotNull List<Generator> generators, List<GeneratedExtension> extensions
    ) {
        Validate.notNull(generators, "generators");
        Validate.validState(!generators.isEmpty(), "generators.isEmpty()");
        Validate.notNull(extensions, "extensions");

        // As per https://developers.google.com/protocol-buffers/docs/reference/java-generated#extension,
        // extensions must be registered in order to be processed.
        ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
        for (GeneratedExtension extension : extensions) {
            extensionRegistry.add(extension);
        }

        try {
            // Parse the input stream to extract the generator request
            byte[] generatorRequestBytes = IOUtils.toByteArray(System.in);
            PluginProtos.CodeGeneratorRequest request = PluginProtos.CodeGeneratorRequest.parseFrom(
                    generatorRequestBytes, extensionRegistry);

            PluginProtos.CodeGeneratorResponse response = generate(generators, request);
            response.writeTo(System.out);

        } catch (GeneratorException ex) {
            try {
                PluginProtos.CodeGeneratorResponse
                        .newBuilder()
                        .setError(ex.getMessage())
                        .build()
                        .writeTo(System.out);
            } catch (IOException ex2) {
                abort(ex2);
            }
        } catch (Throwable ex) { // Catch all the things!
            abort(ex);
        }
    }

    /**
     * Debug a single generator using the parsed proto descriptor.
     *
     * @param generator The generator to run.
     * @param dumpPath  The path to a descriptor dump on the filesystem.
     */
    public static void debug(@NotNull Generator generator, @NotNull String dumpPath) {
        Validate.notNull(generator, "generator");
        debug(Collections.singletonList(generator), dumpPath);
    }

    /**
     * Debug multiple generators using the parsed proto descriptor, aggregating their results.
     *
     * @param generators The list of generators to run.
     * @param dumpPath   The path to a descriptor dump on the filesystem.
     */
    public static void debug(@NotNull List<Generator> generators, @NotNull String dumpPath) {
        debug(generators, Collections.emptyList(), dumpPath);
    }

    /**
     * Debug multiple generators using the parsed proto descriptor, aggregating their results.
     * Also register the given extensions so they may be processed by the generator.
     *
     * @param generators The list of generators to run.
     * @param extensions The list of extensions to register.
     * @param dumpPath   The path to a descriptor dump on the filesystem.
     */
    public static void debug(
            @NotNull List<Generator> generators,
            List<GeneratedExtension> extensions,
            @NotNull String dumpPath
    ) {
        Validate.notNull(generators, "generators");
        Validate.validState(!generators.isEmpty(), "generators.isEmpty()");
        Validate.notNull(extensions, "extensions");
        Validate.notNull(dumpPath, "dumpPath");

        // As per https://developers.google.com/protocol-buffers/docs/reference/java-generated#extension,
        // extensions must be registered in order to be processed.
        ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
        for (GeneratedExtension extension : extensions) {
            extensionRegistry.add(extension);
        }

        try {
            byte[] generatorRequestBytes = FileUtils.readFileToByteArray(new File(dumpPath));
            PluginProtos.CodeGeneratorRequest request = PluginProtos.CodeGeneratorRequest.parseFrom(
                    generatorRequestBytes, extensionRegistry);

            PluginProtos.CodeGeneratorResponse response = generate(generators, request);

            // Print error if present
            if (!StringUtils.isEmpty(response.getError())) {
                System.err.println(response.getError());
            }

            // Write files if present
            for (PluginProtos.CodeGeneratorResponse.File file : response.getFileList()) {
                File outFile;
                if (StringUtils.isEmpty(file.getInsertionPoint())) {
                    outFile = new File(file.getName());
                } else {
                    // Append insertion point to file generatedName
                    String name = FilenameUtils.getBaseName(file.getName()) +
                            "-" +
                            file.getInsertionPoint() +
                            "." +
                            FilenameUtils.getExtension(file.getName());
                    outFile = new File(name);
                }

                FileUtils.createParentDirectories(outFile);
                FileUtils.write(outFile, file.getContent(), Charsets.UTF_8);
//                FileUtils.write(outFile, file.getContentBytes());
            }

        } catch (Throwable ex) { // Catch all the things!
            ex.printStackTrace();
        }
    }

    static PluginProtos.CodeGeneratorResponse generate(
            @NotNull List<Generator> generators,
            @NotNull PluginProtos.CodeGeneratorRequest request
    ) {
        Validate.notNull(generators, "generators");
        Validate.validState(!generators.isEmpty(), "generators.isEmpty()");
        Validate.notNull(request, "request");


        // Run each file generator, collecting the output
        Stream<PluginProtos.CodeGeneratorResponse.File> oldWay = generators
                .stream()
                .flatMap(gen -> gen.generate(request));

        Stream<PluginProtos.CodeGeneratorResponse.File> newWay = generators
                .stream()
                .flatMap(gen -> gen.generateFiles(request).stream());

        int featureMask = generators
                .stream()
                .map(gen -> gen.supportedFeatures().stream())
                // OR each generator's feature set together into a mask
                .map(featureStream -> featureStream.map(PluginProtos.CodeGeneratorResponse.Feature::getNumber)
                        .reduce((l, r) -> l | r)
                        .orElse(PluginProtos.CodeGeneratorResponse.Feature.FEATURE_NONE_VALUE))
                // AND together all the masks
                .reduce((l, r) -> l & r)
                .orElse(PluginProtos.CodeGeneratorResponse.Feature.FEATURE_NONE_VALUE);

        // Send the files back to protoc
        return PluginProtos.CodeGeneratorResponse
                .newBuilder()
                .addAllFile(Stream.concat(oldWay, newWay).collect(Collectors.toList()))
                .setSupportedFeatures(featureMask)
                .build();
    }

    private static void abort(Throwable ex) {
        ex.printStackTrace(System.err);
        System.exit(1);
    }
}
