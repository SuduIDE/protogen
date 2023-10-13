package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.Options;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Message extends EnumOrMessage implements Descriptor {

    private final Descriptors.Descriptor messageDescriptor;

    public Message(Descriptors.Descriptor messageDescriptor) {
        this.messageDescriptor = messageDescriptor;
    }

    public List<Field> getFields() {
        return messageDescriptor.getFields().stream()
                .map(Field::new)
                .toList();
    }

    public boolean isUnfolded() {
        if (getFields().size() != 1) return false;
        return getUnfoldOption().orElse(false);
    }

    /**
     * protobuf internal flag to mark map entries
     *
     * @see <a href="https://protobuf.dev/programming-guides/proto3/#backwards">map specification</a>
     */
    public boolean isMap() {
        return messageDescriptor.getOptions().getMapEntry();
    }

    public Optional<String> getComparatorReference() {
        return Options.wrapExtension(messageDescriptor.getOptions(), protogen.Options.messageComparator);
    }

    @Override
    public @NotNull String getName() {
        return messageDescriptor.getName();
    }

    @Override
    public @NotNull String getFullName() {
        return messageDescriptor.getFullName();
    }

    @Override
    public @NotNull List<? extends EnumOrMessage> getNested() {
        var messages = messageDescriptor.getNestedTypes().stream()
                .map(Message::new);
        var enums = messageDescriptor.getEnumTypes().stream()
                .map(Enum::new);
        return Stream.concat(messages, enums).toList();
    }

    @Override
    public @NotNull File getContainingFile() {
        return new File(messageDescriptor.getFile());
    }

    @Override
    public @Nullable Message getContainingType() {
        return Optional.ofNullable(messageDescriptor.getContainingType())
                .map(Message::new)
                .orElse(null);
    }

    @Override
    public @Nullable String getCustomClass() {
        return Options.wrapExtension(messageDescriptor.getOptions(), protogen.Options.customClass).orElse(null);
    }

    @Override
    public boolean doGenerate() {
        if (isMap()) return getDoGenerateOption().orElse(false);
        if (isUnfolded()) return getDoGenerateOption().orElse(false);
        return super.doGenerate();
    }

    public List<OneOf> getOneofs() {
        return messageDescriptor.getOneofs().stream()
                .map(o -> new OneOf(o, this))
                .toList();
    }

    public Optional<String> getTopic() {
        return Options.wrapExtension(messageDescriptor.getOptions(), protogen.Options.topic);
    }

    // -----------------

    protected Optional<Boolean> getUnfoldOption() {
        return Options.wrapExtension(messageDescriptor.getOptions(), protogen.Options.unfold);
    }

    @Override
    protected Optional<Boolean> getDoGenerateOption() {
        return Options.wrapExtension(messageDescriptor.getOptions(), protogen.Options.genMessage);
    }

    @Override
    protected Optional<String> getOverriddenNameOption() {
        return Options.wrapExtension(messageDescriptor.getOptions(), protogen.Options.messageName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(messageDescriptor, message.messageDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageDescriptor);
    }
}
