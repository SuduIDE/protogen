package org.sudu.protogen.protoc.adaptor;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.protobuf.EnumOrMessage;
import org.sudu.protogen.protoc.Options;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Message extends org.sudu.protogen.protobuf.Message {

    Descriptors.Descriptor descriptor;

    public Message(Descriptors.Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public @NotNull String getName() {
        return descriptor.getName();
    }

    @Override
    public @NotNull String getFullName() {
        return descriptor.getFullName();
    }

    @Override
    public @NotNull File getContainingFile() {
        return new File(descriptor.getFile());
    }

    @Override
    public @Nullable Message getContainingType() {
        return descriptor.getContainingType() == null ? null : new Message(descriptor.getContainingType());
    }

    @Override
    public List<? extends EnumOrMessage> getNested() {
        var messages = descriptor.getNestedTypes().stream()
                .map(Message::new);
        var enums = descriptor.getEnumTypes().stream()
                .map(Enum::new);
        return Stream.concat(messages, enums).toList();
    }

    @Override
    public List<? extends Field> getFields() {
        return descriptor.getFields().stream()
                .map(Field::new)
                .toList();
    }

    @Override
    public Optional<Boolean> getDoGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.genMessage);
    }

    @Override
    public Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.messageName);
    }

    @Override
    public Optional<Boolean> getUnfoldOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.unfold);
    }

    @Override
    protected Optional<String> getCustomClassNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.customClass);
    }

    @Override
    public boolean isMap() {
        return descriptor.getOptions().getMapEntry();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(descriptor, message.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    @Override
    public Optional<String> getComparatorReference() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.messageComparator);
    }
}
