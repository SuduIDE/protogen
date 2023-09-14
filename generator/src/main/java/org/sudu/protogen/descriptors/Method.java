package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.Validate;
import org.sudu.protogen.Options;

import java.util.Objects;
import java.util.Optional;

public class Method {

    private final Descriptors.MethodDescriptor methodDescriptor;

    public Method(Descriptors.MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }

    public String getName() {
        return methodDescriptor.getName();
    }

    public Message getInputType() {
        return new Message(methodDescriptor.getInputType());
    }

    public Message getOutputType() {
        return new Message(methodDescriptor.getOutputType());
    }

    public boolean isInputStreaming() {
        return methodDescriptor.isClientStreaming();
    }

    public boolean isOutputStreaming() {
        return methodDescriptor.isServerStreaming();
    }

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

    public boolean protect() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.protectMethod).orElse(false);
    }


    protected Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.genMethod);
    }

    protected Optional<Boolean> getUnfoldRequestOption() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.unfoldRequest);
    }

    protected Optional<Boolean> getNullableOption() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.nullable);
    }

    public RepeatedContainer getStreamToContainer() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.streamToContainer)
                .map(RepeatedContainer::fromGrpc)
                .orElse(RepeatedContainer.ITERATOR);
    }

    protected Optional<String> getNameOption() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.methodName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return Objects.equals(methodDescriptor, method.methodDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodDescriptor);
    }
}
