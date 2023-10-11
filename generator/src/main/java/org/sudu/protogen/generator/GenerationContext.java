package org.sudu.protogen.generator;

import com.squareup.javapoet.TypeSpec;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.descriptors.Enum;
import org.sudu.protogen.descriptors.*;
import org.sudu.protogen.generator.client.ClientGenerator;
import org.sudu.protogen.generator.enumeration.EnumGenerator;
import org.sudu.protogen.generator.field.FieldGenerator;
import org.sudu.protogen.generator.field.FieldProcessingResult;
import org.sudu.protogen.generator.field.processors.*;
import org.sudu.protogen.generator.message.MessageGenerator;
import org.sudu.protogen.generator.server.ServiceGenerator;
import org.sudu.protogen.generator.type.TypeModel;
import org.sudu.protogen.generator.type.processors.DomainTypeProcessor;
import org.sudu.protogen.generator.type.processors.EmptyMessageProcessor;
import org.sudu.protogen.generator.type.processors.RegisteredTypeProcessor;
import org.sudu.protogen.generator.type.processors.TypeProcessor;

public final class GenerationContext {

    private final Configuration configuration;
    private final GeneratorsHolder holder;
    private final TypeManager typeManager;

    public GenerationContext(Configuration configuration) {
        this.configuration = configuration;
        this.holder = new GeneratorsHolder();
        this.typeManager = new TypeManager();
    }

    public Configuration configuration() {
        return configuration;
    }

    public GeneratorsHolder generatorsHolder() {
        return holder;
    }

    public TypeManager typeManager() {
        return typeManager;
    }

    public class TypeManager {

        private final FieldTypeProcessor fieldTypeProcessor = getFieldProcessingChain();

        private final TypeProcessor typeProcessor = getTypeProcessor();

        public TypeModel processType(EnumOrMessage enumOrMessage) {
            return typeProcessor.processType(enumOrMessage);
        }

        public TypeModel processType(Field field) {
            return fieldTypeProcessor.processType(field);
        }

        public FieldTypeProcessor getFieldProcessingChain() {
            return FieldTypeProcessor.Chain.buildChain( // Ordering is important!
                    new UnfoldedFieldTypeProcessor(GenerationContext.this),
                    new MapFieldTypeProcessor(GenerationContext.this),
                    new ListFieldTypeProcessor(GenerationContext.this),
                    new PrimitiveFieldTypeProcessor(GenerationContext.this),
                    new DomainFieldTypeProcessor(GenerationContext.this)
            );
        }

        public TypeProcessor getTypeProcessor() {
            return TypeProcessor.Chain.buildChain(
                    new RegisteredTypeProcessor(GenerationContext.this),
                    new DomainTypeProcessor(GenerationContext.this),
                    new EmptyMessageProcessor(GenerationContext.this)
            );
        }
    }

    public class GeneratorsHolder {

        private final DescriptorGenerator<Field, FieldProcessingResult> fieldGenerator = new FieldGenerator(GenerationContext.this).withCache();
        private final DescriptorGenerator<Message, TypeSpec> messageGenerator = new MessageGenerator(GenerationContext.this).withCache();
        private final DescriptorGenerator<Enum, TypeSpec> enumGenerator = new EnumGenerator(GenerationContext.this).withCache();
        private final DescriptorGenerator<Service, TypeSpec> clientGenerator = new ClientGenerator(GenerationContext.this).withCache();
        private final DescriptorGenerator<Service, TypeSpec> serviceGenerator = new ServiceGenerator(GenerationContext.this).withCache();

        public FieldProcessingResult generate(Field field) {
            return fieldGenerator.generate(field);
        }

        public TypeSpec generate(Enum anEnum) {
            return enumGenerator.generate(anEnum);
        }

        public TypeSpec generate(Message message) {
            return messageGenerator.generate(message);
        }

        public TypeSpec generate(EnumOrMessage enumOrMessage) {
            if (enumOrMessage instanceof Enum en) return generate(en);
            if (enumOrMessage instanceof Message msg) return generate(msg);
            throw new IllegalStateException();
        }

        public TypeSpec generateClient(Service service) {
            return clientGenerator.generate(service);
        }

        public TypeSpec generateService(Service service) {
            return serviceGenerator.generate(service);
        }
    }
}
