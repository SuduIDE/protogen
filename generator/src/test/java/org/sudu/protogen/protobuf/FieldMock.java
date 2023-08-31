package org.sudu.protogen.protobuf;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class FieldMock extends Field {

    String name;
    String fullName;
    Type type;
    Message messageType;
    Enum enumType;
    boolean repeated;
    boolean optional;
    Optional<String> overriddenNameOption;
    Optional<Boolean> decomposeOption;
    Optional<String> decomposeOverriddenNameOption;
    Optional<Field.RepeatedContainer> repeatedContainerOption;
    Message containingMessage;

    public FieldMock() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Message getMessageType() {
        return this.messageType;
    }

    public void setMessageType(Message messageType) {
        this.messageType = messageType;
    }

    public Enum getEnumType() {
        return this.enumType;
    }

    public void setEnumType(Enum enumType) {
        this.enumType = enumType;
    }

    public boolean isRepeated() {
        return this.repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Optional<String> getOverriddenNameOption() {
        return this.overriddenNameOption;
    }

    public void setOverriddenNameOption(Optional<String> overriddenNameOption) {
        this.overriddenNameOption = overriddenNameOption;
    }

    public Optional<Boolean> getDecomposeOption() {
        return this.decomposeOption;
    }

    public void setDecomposeOption(Optional<Boolean> decomposeOption) {
        this.decomposeOption = decomposeOption;
    }

    public Optional<String> getDecomposeOverriddenNameOption() {
        return this.decomposeOverriddenNameOption;
    }

    public void setDecomposeOverriddenNameOption(Optional<String> decomposeOverriddenNameOption) {
        this.decomposeOverriddenNameOption = decomposeOverriddenNameOption;
    }

    public Optional<RepeatedContainer> getRepeatedContainerOption() {
        return this.repeatedContainerOption;
    }

    public void setRepeatedContainerOption(Optional<RepeatedContainer> repeatedContainerOption) {
        this.repeatedContainerOption = repeatedContainerOption;
    }

    @Override
    protected Optional<Boolean> getUnusedFieldOption() {
        return Optional.empty();
    }

    public Message getContainingMessage() {
        return this.containingMessage;
    }

    public void setContainingMessage(Message containingMessage) {
        this.containingMessage = containingMessage;
    }

    @Override
    public String toString() {
        return "FieldMock{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                ", messageType=" + messageType +
                ", enumType=" + enumType +
                ", repeated=" + repeated +
                ", optional=" + optional +
                ", overriddenNameOption=" + overriddenNameOption +
                ", decomposeOption=" + decomposeOption +
                ", decomposeOverriddenNameOption=" + decomposeOverriddenNameOption +
                ", repeatedContainerOption=" + repeatedContainerOption +
                ", containingMessage=" + containingMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldMock fieldMock = (FieldMock) o;
        return repeated == fieldMock.repeated && optional == fieldMock.optional && Objects.equals(name, fieldMock.name) && Objects.equals(fullName, fieldMock.fullName) && type == fieldMock.type && Objects.equals(messageType, fieldMock.messageType) && Objects.equals(enumType, fieldMock.enumType) && Objects.equals(overriddenNameOption, fieldMock.overriddenNameOption) && Objects.equals(decomposeOption, fieldMock.decomposeOption) && Objects.equals(decomposeOverriddenNameOption, fieldMock.decomposeOverriddenNameOption) && Objects.equals(repeatedContainerOption, fieldMock.repeatedContainerOption) && Objects.equals(containingMessage, fieldMock.containingMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fullName, type, messageType, enumType, repeated, optional, overriddenNameOption, decomposeOption, decomposeOverriddenNameOption, repeatedContainerOption, containingMessage);
    }
}
