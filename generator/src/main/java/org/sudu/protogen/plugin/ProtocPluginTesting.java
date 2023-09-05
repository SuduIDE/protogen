/*
 *  Copyright (c) 2019, Salesforce.com, Inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package org.sudu.protogen.plugin;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.compiler.PluginProtos;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This method is designed to be used by unit tests. Unit testing typically works like this:
 * 1. Add the Dump protoc plugin to your test code generation build phase, to write out a proto dump file.
 * 2. Locate the generated dump file in a unit test.
 * 3. Call this method, with the path of the dump file from a test.
 * 4. Inspect the generated output.
 */
public final class ProtocPluginTesting {
    public static final String MAVEN_DUMP_PATH = "target/generated-test-sources/protobuf/dump/descriptor_dump";

    private ProtocPluginTesting() {

    }

    /**
     * Debug a single generator using the parsed proto descriptor.
     *
     * @param generator The generator to run.
     * @param dumpPath  The path to a descriptor dump on the filesystem.
     * @return The compiled output from the protoc plugin
     */
    public static PluginProtos.CodeGeneratorResponse test(
            @NotNull Generator generator,
            @NotNull String dumpPath
    ) throws IOException {
        Validate.notNull(generator, "generator");
        return test(Collections.singletonList(generator), dumpPath);
    }

    /**
     * Debug multiple generators using the parsed proto descriptor, aggregating their results.
     *
     * @param generators The list of generators to run.
     * @param dumpPath   The path to a descriptor dump on the filesystem.
     * @return The compiled output from the protoc plugin
     */
    public static PluginProtos.CodeGeneratorResponse test(
            @NotNull List<Generator> generators,
            @NotNull String dumpPath
    ) throws IOException {
        return test(generators, Collections.emptyList(), dumpPath);
    }

    /**
     * Test multiple generators using the parsed proto descriptor, aggregating their results.
     * Also register the given extensions so they may be processed by the generator.
     *
     * @param generators The list of generators to run.
     * @param extensions The list of extensions to register.
     * @param dumpPath   The path to a descriptor dump on the filesystem.
     * @return The compiled output from the protoc plugin
     */
    public static PluginProtos.CodeGeneratorResponse test(
            @NotNull List<Generator> generators,
            List<GeneratedMessage.GeneratedExtension> extensions,
            @NotNull String dumpPath
    ) throws IOException {
        Validate.notNull(generators, "generators");
        Validate.validState(!generators.isEmpty(), "generators.isEmpty()");
        Validate.notNull(extensions, "extensions");
        Validate.notNull(dumpPath, "dumpPath");

        // As per https://developers.google.com/protocol-buffers/docs/reference/java-generated#extension,
        // extensions must be registered in order to be processed.
        ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
        for (GeneratedMessage.GeneratedExtension extension : extensions) {
            extensionRegistry.add(extension);
        }

        byte[] generatorRequestBytes = FileUtils.readFileToByteArray(new File(dumpPath));
        PluginProtos.CodeGeneratorRequest request = PluginProtos.CodeGeneratorRequest.parseFrom(
                generatorRequestBytes, extensionRegistry);

        return ProtocPlugin.generate(generators, request);
    }
}
