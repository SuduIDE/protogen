package org.sudu.protogen.utils;

import org.apache.commons.lang3.CharUtils;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.protobuf.File;

public class FileUtils {

    /**
     * In the event of a generatedName conflict between the outer and inner getType names, protoc adds an OuterClass suffix to the
     * outer getType's generatedName.
     */
    public static @NotNull String appendOuterClassSuffix(final String enclosingClassName, File file) {
        if (file.getNested().stream().anyMatch(d -> d.getName().equals(enclosingClassName)) ||
                file.getServices().stream().anyMatch(serviceProto -> serviceProto.getName().equals(enclosingClassName))) {
            return enclosingClassName + "OuterClass";
        } else {
            return enclosingClassName;
        }
    }

    /**
     * Replace invalid proto getIdentifier characters with an underscore, so they will be dropped and camel cases below.
     * <a href="https://developers.google.com/protocol-buffers/docs/reference/proto3-spec">Specification</a>
     */
    public static @NotNull String makeInvalidCharactersUnderscores(String filename) {
        char[] filechars = filename.toCharArray();
        for (int i = 0; i < filechars.length; i++) {
            char c = filechars[i];
            if (!CharUtils.isAsciiAlphanumeric(c)) {
                filechars[i] = '_';
            }
        }
        return new String(filechars);
    }

}
