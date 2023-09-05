package org.sudu.protogen.descriptors;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.config.Configuration;

import java.util.List;
import java.util.Optional;

public abstract class EnumOrMessage {

    public abstract @NotNull String getName();

    public abstract @NotNull String getFullName();

    public abstract @NotNull File getContainingFile();

    public abstract @Nullable Message getContainingType();

    public abstract List<? extends EnumOrMessage> getNested();

    // todo apply
    public final void verify() {
        int optCnt = 0;
        if (getDoGenerateOption().isPresent() && getDoGenerateOption().get()) optCnt++;
        if (getCustomClassNameOption().isPresent()) optCnt++;
        if (this instanceof Message msg) {
            if (msg.getUnfoldOption().isPresent() && msg.getUnfoldOption().get()) optCnt++;
        }
        if (optCnt > 1) {
            throw new IllegalStateException("Only one of generate, decompose or customClass option could be set");
        }
    }

    public final String generatedName(Configuration configuration) {
        Validate.validState(doGenerate(), "Check doGenerate() before calling generatedName()!");
        return getOverriddenNameOption()
                .orElseGet(() -> configuration.namingManager().getDomainName(getName()));
    }

    public final boolean doGenerate() {
        if (this instanceof Message msg) {
            if (msg.isMap()) return getDoGenerateOption().orElse(false);
            if (msg.isUnfolded()) return getDoGenerateOption().orElse(false);
        }
        if (customClass() != null) return false;
        return getDoGenerateOption()
                .orElse(getContainingFile().doGenerate() && !getName().endsWith("Request") && !getName().endsWith("Response"));
    }

    public final boolean isDomain() {
        return doGenerate() || customClass() != null;
    }

    @Nullable
    public final String customClass() {
        return getCustomClassNameOption().orElse(null);
    }

    /*
     * Options are not supposed to be used at high-level logic.
     * They return only the value of an option in .proto file.
     * Advanced logic taking into account other options and configuration values
     * is placed at top-level methods such as getGenerateOption for getGenerateOption.
     */

    protected abstract Optional<String> getCustomClassNameOption();

    protected abstract Optional<Boolean> getDoGenerateOption();

    protected abstract Optional<String> getOverriddenNameOption();
}
