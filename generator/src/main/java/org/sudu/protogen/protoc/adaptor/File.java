package org.sudu.protogen.protoc.adaptor;

import com.google.protobuf.Descriptors;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.protobuf.EnumOrMessage;
import org.sudu.protogen.protoc.Options;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class File extends org.sudu.protogen.protobuf.File {

    Descriptors.FileDescriptor descriptor;

    public File(Descriptors.FileDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public @NotNull String getName() {
        return descriptor.getName();
    }

    @Override
    public @NotNull String getProtoPackage() {
        return descriptor.getPackage();
    }

    @Override
    public @NotNull List<? extends EnumOrMessage> getNested() {
        var messages = descriptor.getMessageTypes().stream()
                .map(Message::new);
        var enums = descriptor.getEnumTypes().stream()
                .map(Enum::new);
        return Stream.concat(messages, enums).toList();
    }

    @Override
    public @NotNull List<? extends Service> getServices() {
        return descriptor.getServices().stream()
                .map(Service::new)
                .toList();
    }

    @Override
    public @NotNull Optional<Boolean> getJavaMultipleFilesOption() {
        return Optional.of(descriptor.getOptions().getJavaMultipleFiles())
                .filter($ -> descriptor.getOptions().hasJavaMultipleFiles());
    }

    @Override
    public @NotNull Optional<String> getJavaOuterClassnameOption() {
        return Optional.of(descriptor.getOptions().getJavaOuterClassname())
                .filter($ -> descriptor.getOptions().hasJavaOuterClassname());
    }

    @Override
    protected @NotNull Optional<String> getProtogenPackageOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.pkg);
    }

    @Override
    public @NotNull Optional<String> getJavaPackageOption() {
        return Optional.of(descriptor.getOptions().getJavaPackage())
                .filter($ -> descriptor.getOptions().hasJavaPackage());
    }

    @Override
    public @NotNull Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.enable);
    }

    @Override
    protected @NotNull Optional<Boolean> getDisableNotNullOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.disableNotnull);
    }
}
