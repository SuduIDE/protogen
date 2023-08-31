package org.sudu.protogen.protoc.adaptor;

import com.google.protobuf.Descriptors;
import org.sudu.protogen.protoc.Options;

import java.util.Objects;
import java.util.Optional;

public class Method extends org.sudu.protogen.protobuf.Method {

    Descriptors.MethodDescriptor descriptor;

    public Method(Descriptors.MethodDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return descriptor.getName();
    }

    @Override
    public Message getInputType() {
        return new Message(descriptor.getInputType());
    }

    @Override
    public Message getOutputType() {
        return new Message(descriptor.getOutputType());
    }

    @Override
    public boolean isInputStreaming() {
        return descriptor.isClientStreaming();
    }

    @Override
    public boolean isOutputStreaming() {
        return descriptor.isServerStreaming();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return Objects.equals(descriptor, method.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    @Override
    protected Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.genMethod);
    }

    @Override
    protected Optional<Boolean> getUnfoldRequestOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.unfoldRequest);
    }

    @Override
    protected Optional<Boolean> getNullableOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.nullable);
    }

    @Override
    protected Optional<Boolean> getStreamToListOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.streamToList);
    }

    @Override
    protected Optional<String> getNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.methodName);
    }
}
