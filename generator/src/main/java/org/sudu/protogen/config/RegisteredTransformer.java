package org.sudu.protogen.config;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RegisteredTransformer(
        String protoType,
        String javaClass,
        TransformRule protoToJava,
        TransformRule javaToProto
) {

    @NotNull
    public static List<RegisteredTransformer> defaultTransformers() {
        return List.of(
                new RegisteredTransformer(
                        "google.protobuf.Timestamp",
                        "java.time.Instant",
                        new TransformRule("$T.ofEpochSecond($L.getSeconds(), $L.getNanos())"),
                        new TransformRule(".setSeconds($L.getEpochSecond()).setNanos($L.getNano())")
                ),
                new RegisteredTransformer(
                        "google.protobuf.Duration",
                        "java.time.Duration",
                        new TransformRule("$T.ofSeconds($L.getSeconds(), $L.getNanos())"),
                        new TransformRule(".setSeconds($L.getSeconds()).setNanos($L.getNano())")
                ),
                new RegisteredTransformer(
                        "google.protobuf.Empty",
                        "void",
                        new TransformRule("$L"),
                        new TransformRule("")
                ),
                new RegisteredTransformer(
                        "org.sudu.api.dvfs.grpc.GrpcContent",
                        "java.io.InputStream",
                        new TransformRule("$L.getBytes().newInput()"),
                        new TransformRule(
                                ".setBytes($t.run(() -> ByteString.readFrom($L)))",
                                ClassName.get("org.sudu.util", "Unchecked")
                        )
                )
        );
    }

    public record TransformRule(String rule, TypeName... params) {
    }
}
