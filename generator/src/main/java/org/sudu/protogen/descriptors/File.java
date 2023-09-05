package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.protoc.Options;
import org.sudu.protogen.utils.FileUtils;
import org.sudu.protogen.utils.Name;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class File {

    private final Descriptors.FileDescriptor descriptor;

    public File(Descriptors.FileDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public @NotNull String getName() {
        return descriptor.getName();
    }

    public @NotNull String getProtoPackage() {
        return descriptor.getPackage();
    }

    public @NotNull List<? extends EnumOrMessage> getNested() {
        var messages = descriptor.getMessageTypes().stream()
                .map(Message::new);
        var enums = descriptor.getEnumTypes().stream()
                .map(Enum::new);
        return Stream.concat(messages, enums).toList();
    }

    public @NotNull List<? extends Service> getServices() {
        return descriptor.getServices().stream()
                .map(Service::new)
                .toList();
    }

    // =============

    public final @NotNull String getGeneratePackage() {
        return getProtogenPackageOption()
                .orElse(StringUtils.removeEnd(getJavaPackage(), ".grpc"));
    }

    public final @NotNull String getJavaPackage() {
        return getJavaPackageOption()
                .orElse(getProtoPackage());
    }

    public final @NotNull String getJavaOuterClassname() {
        if (getJavaOuterClassnameOption().isPresent()) {
            return getJavaOuterClassnameOption().get();
        }

        // If the outer class generatedName is not explicitly defined, then we take the proto filename, strip its extension,
        // and convert it from snake case to camel case.
        String filename = getName().substring(0, getName().length() - ".proto".length());

        // Protos in subdirectories without java_outer_classname have their path prepended to the filename. Remove
        // if present.
        if (filename.contains("/")) {
            filename = filename.substring(filename.lastIndexOf('/') + 1);
        }

        filename = FileUtils.makeInvalidCharactersUnderscores(filename);
        filename = Name.toCamelCase(filename);
        filename = FileUtils.appendOuterClassSuffix(filename, this);
        return filename;
    }

    @Nullable
    public final String getEnclosingClass() {
        return getJavaMultipleFiles() ? null : getJavaOuterClassname();
    }

    public final boolean getJavaMultipleFiles() {
        return getJavaMultipleFilesOption().orElse(false);
    }

    // =============

    /**
     * @return Whether generation of <b>all</b> entries of file is required.
     * However, it doesn't applicable to determine whether scanning of the file is required.
     * Primarily created for EnumOrMessage#goGenerate
     */
    public final boolean doGenerate() {
        return getGenerateOption().orElse(false);
    }

    public final boolean doUseNullabilityAnnotation(boolean isNullable) {
        // !nullable -> disable_notnull == false  <=>   nullable || disable_notnull == false
        return isNullable || !getDisableNotNullOption().orElse(false);
    }

    // =============

    /*
     * Options are not supposed to be used at high-level logic.
     * They return only the value of an option in .proto file.
     * Advanced logic taking into account other options and configuration values
     * is placed at top-level methods such as doGenerate for getGenerateOption.
     */

    protected @NotNull Optional<Boolean> getJavaMultipleFilesOption() {
        return Optional.of(descriptor.getOptions().getJavaMultipleFiles())
                .filter($ -> descriptor.getOptions().hasJavaMultipleFiles());
    }

    protected @NotNull Optional<String> getJavaOuterClassnameOption() {
        return Optional.of(descriptor.getOptions().getJavaOuterClassname())
                .filter($ -> descriptor.getOptions().hasJavaOuterClassname());
    }

    protected @NotNull Optional<String> getProtogenPackageOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.pkg);
    }

    protected @NotNull Optional<String> getJavaPackageOption() {
        return Optional.of(descriptor.getOptions().getJavaPackage())
                .filter($ -> descriptor.getOptions().hasJavaPackage());
    }

    protected @NotNull Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.enable);
    }

    protected @NotNull Optional<Boolean> getDisableNotNullOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.disableNotnull);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(descriptor, file.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }
}
