package org.sudu.protogen.generator;

import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sudu.protogen.ProtogenException;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.EnumOrMessage;
import org.sudu.protogen.descriptors.File;
import org.sudu.protogen.utils.Name;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeTable {

    private final HashMap<EnumOrMessage, ClassName> table;

    private TypeTable(HashMap<EnumOrMessage, ClassName> table) {
        this.table = table;
    }

    public static TypeTable makeProtoTypeTable(Iterable<? extends File> files, Configuration configuration) {
        return new ProtoTypeTableBuilder().discover(files, configuration);
    }

    public static TypeTable makeDomainTypeTable(Iterable<? extends File> files, Configuration configuration) {
        return new DomainTypeTableBuilder().discover(files, configuration);
    }

    public boolean containsType(EnumOrMessage enumOrMessage) {
        return table.containsKey(enumOrMessage);
    }

    @NotNull
    public ClassName getType(EnumOrMessage enumOrMessage) {
        return Optional.ofNullable(table.get(enumOrMessage))
                .orElseThrow(() -> new ProtogenException("Type not found for " + enumOrMessage.getFullName()));
    }

    private abstract static class AbstractTypeTableBuilder {

        protected TypeTable discover(Iterable<? extends File> files, Configuration configuration) {
            HashMap<EnumOrMessage, ClassName> typesBuilder = new HashMap<>();
            for (var file : files) {
                file.getNested().forEach(d -> successor(d, typesBuilder, configuration));
            }
            return new TypeTable(typesBuilder);
        }

        private void successor(
                EnumOrMessage enumOrMessage,
                HashMap<EnumOrMessage, ClassName> typesBuilder,
                Configuration configuration
        ) {
            if (!filter(enumOrMessage)) {
                return;
            }
            if (enumOrMessage.getContainingType() == null) {
                String javaPackage = getJavaPackage(enumOrMessage);
                String enclosingClass = getEnclosingClass(enumOrMessage, configuration);
                typesBuilder.put(
                        enumOrMessage,
                        ClassName.get(javaPackage, dotJoiner(enclosingClass, getDescriptorName(enumOrMessage, configuration)))
                );
            } else {
                ClassName containing = typesBuilder.get(enumOrMessage.getContainingType());
                typesBuilder.put(
                        enumOrMessage,
                        ClassName.get(containing.packageName(), containing.simpleName(), getDescriptorName(enumOrMessage, configuration))
                );
            }
            enumOrMessage.getNested().forEach(d -> successor(d, typesBuilder, configuration));
        }

        @NotNull
        protected abstract String getJavaPackage(EnumOrMessage enumOrMessage);

        protected abstract @Nullable String getEnclosingClass(
                @NotNull EnumOrMessage enumOrMessage,
                Configuration configuration
        );

        protected abstract @NotNull String getDescriptorName(
                @NotNull EnumOrMessage enumOrMessage,
                Configuration configuration
        );

        protected boolean filter(EnumOrMessage enumOrMessage) {
            return true;
        }

        private String dotJoiner(String... strings) {
            return Arrays.stream(strings).filter(Objects::nonNull).collect(Collectors.joining("."));
        }
    }

    private static final class ProtoTypeTableBuilder extends AbstractTypeTableBuilder {

        private ProtoTypeTableBuilder() {
        }

        @Override
        protected @NotNull String getJavaPackage(EnumOrMessage enumOrMessage) {
            return enumOrMessage.getContainingFile().getJavaPackage();
        }

        @Override
        protected @Nullable String getEnclosingClass(
                @NotNull EnumOrMessage enumOrMessage,
                Configuration configuration
        ) {
            return enumOrMessage.getContainingFile().getEnclosingClass();
        }

        @Override
        protected @NotNull String getDescriptorName(@NotNull EnumOrMessage enumOrMessage, Configuration configuration) {
            return enumOrMessage.getName();
        }
    }

    private static final class DomainTypeTableBuilder extends AbstractTypeTableBuilder {

        private DomainTypeTableBuilder() {
        }

        @Override
        protected @NotNull String getJavaPackage(EnumOrMessage enumOrMessage) {
            String customClass = enumOrMessage.customClass();
            if (customClass != null) {
                return Name.getPackage(customClass);
            }
            return enumOrMessage.getContainingFile().getGeneratePackage();
        }

        @Override
        protected @Nullable String getEnclosingClass(
                @NotNull EnumOrMessage enumOrMessage,
                Configuration configuration
        ) {
            return null;
        }

        @Override
        protected @NotNull String getDescriptorName(@NotNull EnumOrMessage enumOrMessage, Configuration configuration) {
            String customClass = enumOrMessage.customClass();
            if (customClass != null) {
                return Name.getLastName(customClass);
            }
            return enumOrMessage.generatedName(configuration);
        }

        @Override
        protected boolean filter(EnumOrMessage enumOrMessage) {
            return enumOrMessage.doGenerate() || enumOrMessage.customClass() != null;
        }
    }
}