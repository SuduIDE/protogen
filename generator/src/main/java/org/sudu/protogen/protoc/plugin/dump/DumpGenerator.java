/*
 *  Copyright (c) 2019, Salesforce.com, Inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package org.sudu.protogen.protoc.plugin.dump;

import com.google.protobuf.compiler.PluginProtos;
import org.sudu.protogen.protoc.plugin.Generator;
import org.sudu.protogen.protoc.plugin.GeneratorException;
import org.sudu.protogen.protoc.plugin.ProtocPlugin;

import java.util.Collections;
import java.util.List;

/**
 * Dumps the content of the input descriptor set to descriptor_dump.json.
 */
public class DumpGenerator extends Generator {

    public static void main(String[] args) {
        ProtocPlugin.generate(new DumpGenerator());
    }

    @Override
    protected List<PluginProtos.CodeGeneratorResponse.Feature> supportedFeatures() {
        return Collections.singletonList(PluginProtos.CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL);
    }

    @Override
    public List<PluginProtos.CodeGeneratorResponse.File> generateFiles(PluginProtos.CodeGeneratorRequest request) throws GeneratorException {
        return Collections.singletonList(
                makeFile("descriptor_dump", request.toByteArray())
//                    makeFile("descriptor_dump.json", JsonFormat.printer().printEmittable(request))
        );
    }
}
