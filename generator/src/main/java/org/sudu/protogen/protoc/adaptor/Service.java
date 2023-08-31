package org.sudu.protogen.protoc.adaptor;

import com.google.protobuf.Descriptors;
import org.sudu.protogen.protoc.Options;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Service extends org.sudu.protogen.protobuf.Service {

    Descriptors.ServiceDescriptor descriptor;

    public Service(Descriptors.ServiceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return descriptor.getName();
    }

    @Override
    public File getContainingFile() {
        return new File(descriptor.getFile());
    }

    @Override
    public List<? extends Method> getMethods() {
        return descriptor.getMethods().stream()
                .map(Method::new)
                .toList();
    }

    @Override
    protected Optional<Boolean> getAbstractOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.abstract_);
    }

    @Override
    protected Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.genService);
    }

    @Override
    protected Optional<String> getNameOption() {
        return Options.wrapExtension(descriptor.getOptions(), protogen.Options.serviceName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(descriptor, service.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }
}
