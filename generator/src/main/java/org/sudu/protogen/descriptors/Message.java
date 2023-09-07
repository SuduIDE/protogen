package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.Options;
import org.sudu.protogen.generator.GenerationContext;
import org.sudu.protogen.generator.message.MessageGenerator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Message extends EnumOrMessage {

    private final Descriptors.Descriptor descriptor;

    public Message(Descriptors.Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public List<Field> getFields() {
        return descriptor.getFields().stream()
                .map(Field::new)
                .toList();
    }

    public final boolean isUnfolded() {
        if (getFields().size() != 1) return false;
        return getUnfoldOption().orElse(false);
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
    public TypeSpec generate(GenerationContext context) {
        return new MessageGenerator(context, this).generate();
    }

    /*
     * Options are not supposed to be used at high-level logic.
     * They return only the value of an option in .proto file.
     * Advanced logic taking into account other options and configuration values
     * is placed at top-level methods such as isUnfolded for getUnfoldOption.
     */

    /**
     * protobuf internal flag to tag map entries
     *
     * @see <a href="https://protobuf.dev/programming-guides/proto3/#backwards">map specification</a>
     */

    @Override
    protected Optional<Boolean> getDoGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.genMessage);
    }

    @Override
    protected Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.messageName);
    }

    @Override
    protected Optional<String> getCustomClassNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.customClass);
    }

    public boolean isMap() {
        return descriptor.getOptions().getMapEntry();
    }

    public Optional<String> getComparatorReference() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.messageComparator);
    }

    public Optional<Boolean> getUnfoldOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.unfold);
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
}
