package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.Options;
import org.sudu.protogen.generator.type.TypeModel;

import javax.lang.model.element.Modifier;
import java.util.Objects;
import java.util.Optional;

public class Method implements Descriptor {

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

    public final protogen.Options.IfNotFound ifNotFoundBehavior() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.ifNotFound)
                .orElse(protogen.Options.IfNotFound.IGNORE);
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
        return getUnfoldRequestOption().orElse(false);
    }

    public final boolean doUnfoldResponse(@Nullable TypeModel responseType) {
        return responseType == null && getOutputType().getFields().size() == 1;
    }

    public final Field unfoldedResponseField() {
        return getOutputType().getFields().get(0);
    }

    public Modifier getAccessModifier() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.accessModifier)
                .map(option -> switch (option) {
                    case PUBLIC -> Modifier.PUBLIC;
                    case PRIVATE -> Modifier.PRIVATE;
                    case PROTECTED -> Modifier.PROTECTED;
                    case UNRECOGNIZED -> throw new IllegalStateException();
                })
                .orElse(Modifier.PUBLIC);
    }


    protected Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.genMethod);
    }

    protected Optional<Boolean> getUnfoldRequestOption() {
        return Options.wrapExtension(methodDescriptor.getOptions(), protogen.Options.unfoldRequest);
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
