package org.sudu.protogen.protobuf;

import org.apache.commons.lang3.Validate;

import java.util.Optional;

public abstract class Method {

    public abstract String getName();

    public abstract Message getInputType();

    public abstract Message getOutputType();

    public abstract boolean isInputStreaming();

    public abstract boolean isOutputStreaming();

    public final boolean isNullable() {
        return getNullableOption().orElse(false);
    }

    public final String generatedName() {
        return getNameOption().orElseGet(this::getName);
    }

    public final boolean doGenerate() {
        if (isInputStreaming()) return false;
        return getGenerateOption()
                .orElse(true);
    }

    public final boolean doUnfoldRequest() {
        return getUnfoldRequestOption()
                .orElse(!getInputType().isDomain());
    }

    public final boolean doUnfoldResponse() {
        return !getOutputType().isDomain() && getOutputType().getFields().size() == 1;
    }

    public final Field unfoldedResponseField() {
        Validate.validState(doUnfoldResponse());
        return getOutputType().getFields().get(0);
    }

    protected abstract Optional<Boolean> getGenerateOption();

    protected abstract Optional<Boolean> getUnfoldRequestOption();

    protected abstract Optional<Boolean> getNullableOption();

    public abstract Optional<RepeatedContainer> getStreamToContainer();

    protected abstract Optional<String> getNameOption();
}
