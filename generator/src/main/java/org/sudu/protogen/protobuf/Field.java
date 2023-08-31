package org.sudu.protogen.protobuf;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import protogen.Options;

import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Field {

    public abstract String getName();

    public abstract String getFullName();

    public abstract Type getType();

    @Nullable
    public abstract Message getMessageType();

    @Nullable
    public abstract Enum getEnumType();

    public abstract Message getContainingMessage();

    public final boolean isNullable() {
        return isOptional() || (isUnfolded() && getUnfoldedField().isNullable());
    }

    public final boolean isList() {
        return isRepeated();
    }

    public final boolean isMap() {
//        noinspection DataFlowIssue because getMessageType() != null iff type == MESSAGE
        return getType() == Type.MESSAGE
                && getMessageType().isMap();
    }

    public final boolean isUnfolded() {
        //noinspection DataFlowIssue because getMessageType() != null iff type == MESSAGE
        return getType() == Type.MESSAGE && getMessageType().isUnfolded();
    }

    public final boolean isIgnored() {
        return getUnusedFieldOption().orElse(false);
    }

    public final Field getUnfoldedField() {
        //noinspection DataFlowIssue because isUnfolded() true iff getMessageType() != null
        Validate.validState(isUnfolded());
        return getMessageType().getFields().get(0);
    }

    public final String getGeneratedName() {
        return getOverriddenNameOption()
                .orElseGet(() -> isUnfolded() ? getUnfoldedField().getGeneratedName() : getName());

    }

    public final RepeatedContainer getRepeatedContainer() {
        return getRepeatedContainerOption().orElse(RepeatedContainer.LIST);
    }

    /*
     * Options are not supposed to be used at high-level logic.
     * They return only the value of an option in .proto file.
     * Advanced logic taking into account other options and configuration values
     * is placed at top-level methods such as getGenerateOption for getGenerateOption.
     */

    public abstract boolean isRepeated();

    public abstract boolean isOptional();

    protected abstract Optional<String> getOverriddenNameOption();

    protected abstract Optional<RepeatedContainer> getRepeatedContainerOption();

    protected abstract Optional<Boolean> getUnusedFieldOption();

    public enum Type {
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        STRING,
        BYTE_STRING,
        ENUM,
        MESSAGE
    }

    public enum RepeatedContainer {
        LIST(ClassName.get("java.util", "List"), CodeBlock.of("$T.toList()", Collectors.class)),
        SET(ClassName.get("java.util", "Set"), CodeBlock.of("$T.toSet()", Collectors.class));

        private final ClassName typeName;
        private final CodeBlock collectorExpr;

        RepeatedContainer(ClassName typeName, CodeBlock collectorExpr) {
            this.typeName = typeName;
            this.collectorExpr = collectorExpr;
        }

        public static RepeatedContainer fromGrpc(Options.RepeatedContainer proto) {
            return switch (proto) {
                case UNRECOGNIZED -> throw new IllegalArgumentException();
                case LIST -> RepeatedContainer.LIST;
                case SET -> RepeatedContainer.SET;
            };
        }

        public ClassName getTypeName() {
            return typeName;
        }

        public CodeBlock getCollectorExpr() {
            return collectorExpr;
        }
    }
}
