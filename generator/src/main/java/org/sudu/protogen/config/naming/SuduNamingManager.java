package org.sudu.protogen.config.naming;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public class SuduNamingManager implements NamingManager {

    private static final String prefix = "Grpc";

    @Override
    public @NotNull String getDomainName(@NotNull String messageName) {
        Validate.validState(
                messageName.startsWith(prefix),
                "Naming policy violation for %s. Its name should start with Grpc",
                messageName
        );
        return StringUtils.removeStart(messageName, prefix);
    }
}
