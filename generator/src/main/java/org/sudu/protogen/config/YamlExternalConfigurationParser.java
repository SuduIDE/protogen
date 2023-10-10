package org.sudu.protogen.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.sudu.protogen.utils.Name;

import java.io.File;
import java.io.IOException;

public class YamlExternalConfigurationParser implements Configuration.Parser {

    private static final ObjectMapper objectMapper = configureObjectMapper();

    @NotNull
    private final String configDirectory;

    public YamlExternalConfigurationParser(@NotNull String configDirectory) {
        this.configDirectory = configDirectory;
    }

    @NotNull
    private static ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TypeName.class, new TypeNameDeserializer());
        mapper.registerModule(module);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }

    @Override
    public Configuration parse() {
        Configuration.Builder builder = new Configuration.Builder();
        File[] configs = new File(configDirectory).listFiles((dir, name) -> name.endsWith("protogen.yaml"));
        if (configs == null) {
            throw new IllegalArgumentException("Can't find a config directory");
        }
        for (File file : configs) {
            if (file.getName().equals("protogen.yaml")) {
                builder.merge(parseConfigAss(file, GeneralConfiguration.class, new GeneralConfiguration()));
            } else {
                builder.addFileConfiguration(file.getName().replace(".protogen.yaml", ""), parseConfigAss(file, FileConfiguration.class, new FileConfiguration()));
            }
        }
        return builder.build();
    }

    private static <T> T parseConfigAss(File configFile, Class<T> clazz, T empty) {
        try {
            return objectMapper.readValue(configFile, clazz);
        }
        catch (Exception e) {
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
