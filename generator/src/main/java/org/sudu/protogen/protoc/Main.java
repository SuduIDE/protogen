package org.sudu.protogen.protoc;

import org.sudu.protogen.protoc.plugin.ProtocPlugin;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        var generator = new ProtogenGenerator();
        var extensions = generator.getExtensions();
        if (args.length == 0) {
            ProtocPlugin.generate(List.of(generator), extensions);
        } else {
            ProtocPlugin.debug(List.of(generator), extensions, args[0]);
        }
    }
}
