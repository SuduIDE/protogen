package org.sudu.protogen.descriptors;

import com.google.protobuf.Descriptors;
import com.squareup.javapoet.ClassName;
import org.sudu.protogen.Options;
import org.sudu.protogen.utils.Name;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Service implements Descriptor {

    private final Descriptors.ServiceDescriptor serviceDescriptor;

    public Service(Descriptors.ServiceDescriptor serviceDescriptor) {
        this.serviceDescriptor = serviceDescriptor;
    }

    public String getName() {
        return serviceDescriptor.getName();
    }

    public File getContainingFile() {
        return new File(serviceDescriptor.getFile());
    }

    public List<? extends Method> getMethods() {
        return serviceDescriptor.getMethods().stream()
                .map(Method::new)
                .toList();
    }

    public final boolean isAbstract() {
        return getAbstractOption().orElse(false);
    }

    public final boolean doGenerate() {
        return getGenerateOption().orElse(getContainingFile().doEnableGenerator());
    }

    public final String generatedClientName() {
        return getNameOption()
                .orElse("Default" + Name.toCamelCase(getName().replace("Service", "")) + "Client");
    }

    public final String generatedServiceName() {
        return "Base" + Name.toCamelCase(getName().replace("Service", "")) + "Service";
    }

    public final ClassName stubClass() {
        String stubTypeName = getName() + "Grpc";
        String packageName = getContainingFile().getJavaPackage();
        return ClassName.get(packageName, stubTypeName);
    }

    public final ClassName blockingStubClass() {
        ClassName stubClass = stubClass();
        return ClassName.get(stubClass.packageName(), stubClass.simpleName() + "." + getName() + "BlockingStub");
    }

    protected Optional<Boolean> getAbstractOption() {
        return Options.wrapExtension(serviceDescriptor.getOptions(), protogen.Options.abstract_);
    }

    protected Optional<Boolean> getGenerateOption() {
        return Options.wrapExtension(serviceDescriptor.getOptions(), protogen.Options.genService);
    }

    protected Optional<String> getNameOption() {
        return Options.wrapExtension(serviceDescriptor.getOptions(), protogen.Options.serviceName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(serviceDescriptor, service.serviceDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceDescriptor);
    }
}
