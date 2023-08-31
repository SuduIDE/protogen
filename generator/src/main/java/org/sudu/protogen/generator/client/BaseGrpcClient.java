package org.sudu.protogen.generator.client;

import com.squareup.javapoet.*;
import org.sudu.protogen.utils.Poem;

import javax.lang.model.element.Modifier;
import java.util.List;

public class BaseGrpcClient {

    public static final TypeName clazz = ClassName.get("org.sudu.api", "BaseGrpcClient");

    private static final ClassName managedChannelClass = ClassName.get("io.grpc", "ManagedChannel");
    public static final FieldSpec channel = FieldSpec.builder(managedChannelClass, "channel").build();

    private static final ParameterSpec endpointParam = Poem.parameter(ClassName.get(String.class), "endpoint");
    private static final ParameterSpec hostParam = Poem.parameter(ClassName.get(String.class), "host");
    private static final ParameterSpec portParam = Poem.parameter(TypeName.INT, "port");
    private static final ParameterSpec channelParam = Poem.parameter(managedChannelClass, "channel");


    public static List<MethodSpec> generateConstructors(CodeBlock constructorBody) {
        return List.of(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(BaseGrpcClient.endpointParam)
                        .addStatement("super($N)", BaseGrpcClient.endpointParam)
                        .addCode(constructorBody)
                        .build(),
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(BaseGrpcClient.hostParam)
                        .addParameter(BaseGrpcClient.portParam)
                        .addStatement("super($N, $N)", BaseGrpcClient.hostParam, BaseGrpcClient.portParam)
                        .addCode(constructorBody)
                        .build(),
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(BaseGrpcClient.channelParam)
                        .addStatement("super($N)", BaseGrpcClient.channelParam)
                        .addCode(constructorBody)
                        .build()
        );
    }
}
