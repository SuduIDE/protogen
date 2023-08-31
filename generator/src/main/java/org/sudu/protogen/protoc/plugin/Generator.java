/*
 *  Copyright (c) 2019, Salesforce.com, Inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package org.sudu.protogen.protoc.plugin;

import com.google.protobuf.ByteString;
import com.google.protobuf.compiler.PluginProtos;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Generator is the base class for all protoc generators managed by {@link ProtocPlugin}.
 */
public abstract class Generator {

    /**
     * Processes a generator request into a set of files to output.
     *
     * @param request The raw generator request from protoc.
     * @return The completed files to write out.
     * @deprecated use {@link #generateFiles(PluginProtos.CodeGeneratorRequest)} and return a List instead of a Stream.
     */
    @Deprecated()
    public Stream<PluginProtos.CodeGeneratorResponse.File> generate(PluginProtos.CodeGeneratorRequest request) throws GeneratorException {
        return Stream.empty();
    }

    /**
     * Processes a generator request into a set of files to output.
     *
     * @param request The raw generator request from protoc.
     * @return The completed files to write out.
     */
    public List<PluginProtos.CodeGeneratorResponse.File> generateFiles(PluginProtos.CodeGeneratorRequest request) throws GeneratorException {
        return Collections.emptyList();
    }

    /**
     * Signals to protoc which additional generator features this Generator supports. By default, this method returns
     * FEATURE_NONE. You must override this method and supply a value, like FEATURE_PROTO3_OPTIONAL.
     *
     * @return A list of enumerated features.
     */
    protected List<PluginProtos.CodeGeneratorResponse.Feature> supportedFeatures() {
        return Collections.singletonList(PluginProtos.CodeGeneratorResponse.Feature.FEATURE_NONE);
    }

    /**
     * Creates a protobuf file message from a given generatedName and content.
     *
     * @param fileName    The generatedName of the file to getGenerateOption.
     * @param fileContent The content of the generated file.
     * @return The protobuf file.
     */
    protected PluginProtos.CodeGeneratorResponse.File makeFile(String fileName, String fileContent) {
        return PluginProtos.CodeGeneratorResponse.File
                .newBuilder()
                .setName(fileName)
                .setContent(fileContent)
                .build();
    }

    /**
     * Creates a protobuf file message from a given generatedName and content.
     *
     * @param fileName    The generatedName of the file to getGenerateOption.
     * @param fileContent The content of the generated file.
     * @return The protobuf file.
     */
    protected PluginProtos.CodeGeneratorResponse.File makeFile(String fileName, byte[] fileContent) {
        return PluginProtos.CodeGeneratorResponse.File
                .newBuilder()
                .setName(fileName)
                .setContentBytes(ByteString.copyFrom(fileContent))
                .build();
    }
}
