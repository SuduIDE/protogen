package org.sudu.protogen.config.naming;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public class SuduNamingManager implements NamingManager {

    private static final String PREFIX = "Grpc";

    @Override
    public @NotNull String manageName(@NotNull String originalName) {
        Validate.validState(
                originalName.startsWith(PREFIX),
                "Naming policy violation for %s. Its name should start with \"Grpc\"", originalName
        );
        return StringUtils.removeStart(originalName, PREFIX);
    }
}
