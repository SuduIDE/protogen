package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.Options;
import org.sudu.protogen.utils.FileUtils;
import org.sudu.protogen.utils.Name;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class File implements Descriptor {

    private final Descriptors.FileDescriptor fileDescriptor;

    public File(Descriptors.FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
    }

    public @NotNull String getName() {
        return fileDescriptor.getName();
    }

    public @NotNull String getProtoPackage() {
        return fileDescriptor.getPackage();
    }

    public @NotNull List<? extends EnumOrMessage> getNested() {
        var messages = fileDescriptor.getMessageTypes().stream()
                .map(Message::new);
        var enums = fileDescriptor.getEnumTypes().stream()
                .map(Enum::new);
        return Stream.concat(messages, enums).toList();
    }

    public @NotNull List<? extends Service> getServices() {
        return fileDescriptor.getServices().stream()
                .map(Service::new)
                .toList();
    }

    // =============

    public @NotNull String getGeneratePackage() {
        return getProtogenPackageOption()
                .orElse(StringUtils.removeEnd(getJavaPackage(), ".grpc"));
    }

    public @NotNull String getJavaPackage() {
        return getJavaPackageOption()
                .orElse(getProtoPackage());
    }

    public @NotNull String getJavaOuterClassname() {
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

    public @Nullable String getEnclosingClass() {
        return getJavaMultipleFiles() ? null : getJavaOuterClassname();
    }

    public boolean getJavaMultipleFiles() {
        return fileDescriptor.getOptions().getJavaMultipleFiles();
    }

    // =============

    public boolean doEnableGenerator() {
        return getEnableOption().orElse(false);
    }

    public boolean doUseNullabilityAnnotation(boolean isNullable) {
        // disable_notnull -> nullable
        return !getDisableNotNullOption().orElse(false) || isNullable;
    }

    // =============

    protected @NotNull Optional<String> getJavaOuterClassnameOption() {
        return Optional.of(fileDescriptor.getOptions().getJavaOuterClassname())
                .filter($ -> fileDescriptor.getOptions().hasJavaOuterClassname());
    }

    protected @NotNull Optional<String> getProtogenPackageOption() {
        return Options.wrapExtension(fileDescriptor.getOptions(), protogen.Options.pkg);
    }

    protected @NotNull Optional<String> getJavaPackageOption() {
        return Optional.of(fileDescriptor.getOptions().getJavaPackage())
                .filter($ -> fileDescriptor.getOptions().hasJavaPackage());
    }

    protected @NotNull Optional<Boolean> getEnableOption() {
        return Options.wrapExtension(fileDescriptor.getOptions(), protogen.Options.enable);
    }

    protected @NotNull Optional<Boolean> getDisableNotNullOption() {
        return Options.wrapExtension(fileDescriptor.getOptions(), protogen.Options.disableNotnull);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(fileDescriptor, file.fileDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileDescriptor);
    }
}
