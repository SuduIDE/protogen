package org.sudu.protogen.config.naming;

import org.jetbrains.annotations.NotNull;

public interface NamingManager {

    @NotNull
    String manageName(@NotNull String originalName);
}
