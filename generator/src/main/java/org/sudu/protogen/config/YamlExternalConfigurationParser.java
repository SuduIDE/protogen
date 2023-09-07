package org.sudu.protogen.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.utils.Name;

import java.io.File;
import java.io.IOException;

public class YamlExternalConfigurationParser implements ExternalConfiguration.Parser {

    private static final ObjectMapper objectMapper = configureObjectMapper();
    private final String filePath;

    public YamlExternalConfigurationParser(String filePath) {
        this.filePath = filePath;
    }

    @NotNull
    private static ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TypeName.class, new TypeNameDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    @Override
    public ExternalConfiguration parse() {
        try {
            return objectMapper.readValue(new File(filePath), ExternalConfiguration.class);
        } catch (MismatchedInputException e) {
            return ExternalConfiguration.EMPTY;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static class TypeNameDeserializer extends StdDeserializer<TypeName> {

        protected TypeNameDeserializer() {
            super(TypeName.class);
        }

        @Override
        public TypeName deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            String fullyQualified = node.textValue();
            return ClassName.get(Name.getPackage(fullyQualified), Name.getLastName(fullyQualified));
        }
    }
}
