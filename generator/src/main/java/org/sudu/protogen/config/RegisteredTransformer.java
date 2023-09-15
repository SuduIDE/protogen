package org.sudu.protogen.config;

import com.google.protobuf.ByteString;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public record RegisteredTransformer(
        String protoType,
        TypeName javaClass,
        TransformRule protoToJava,
        TransformRule javaToProto
) {

    @NotNull
    public static List<RegisteredTransformer> defaultTransformers() {
        RegisteredTransformer emptyTransformer = new RegisteredTransformer(
                "google.protobuf.Empty",
                TypeName.VOID,
                new TransformRule("$L"),
                new TransformRule("")
        );
        RegisteredTransformer timestampTransformer = new RegisteredTransformer(
                "google.protobuf.Timestamp",
                ClassName.get(Instant.class),
                new TransformRule("$T.ofEpochSecond($L.getSeconds(), $L.getNanos())"),
                new TransformRule(".setSeconds($L.getEpochSecond()).setNanos($L.getNano())")
        );
        RegisteredTransformer durationTransformer = new RegisteredTransformer(
                "google.protobuf.Duration",
                ClassName.get(Duration.class),
                new TransformRule("$T.ofSeconds($L.getSeconds(), $L.getNanos())"),
                new TransformRule(".setSeconds($L.getSeconds()).setNanos($L.getNano())")
        );
        RegisteredTransformer byteStringTransformer = new RegisteredTransformer(
                "google.protobuf.BytesValue",
                ClassName.get(ByteString.class),
                new TransformRule("$L.getValue()"),
                new TransformRule(".setValue($L)")
        );
        return Stream.concat(
                Stream.of(
                        timestampTransformer,
                        durationTransformer,
                        emptyTransformer,
                        byteStringTransformer
                ),
                Stream.of(
                        makeWrapperTransformer("google.protobuf.DoubleValue", TypeName.DOUBLE),
                        makeWrapperTransformer("google.protobuf.FloatValue", TypeName.FLOAT),
                        makeWrapperTransformer("google.protobuf.Int64Value", TypeName.LONG),
                        makeWrapperTransformer("google.protobuf.UInt64Value", TypeName.LONG),
                        makeWrapperTransformer("google.protobuf.Int32Value", TypeName.INT),
                        makeWrapperTransformer("google.protobuf.UInt32Value", TypeName.INT),
                        makeWrapperTransformer("google.protobuf.BoolValue", TypeName.BOOLEAN),
                        makeWrapperTransformer("google.protobuf.StringValue", ClassName.get(String.class))
                )
        ).toList();
    }

    @NotNull
    private static RegisteredTransformer makeWrapperTransformer(String wrapperType, TypeName javaClass) {
        return new RegisteredTransformer(
                wrapperType,
                javaClass,
                new TransformRule("$L.getValue()"),
                new TransformRule(".setValue($L)")
        );
    }

    public record TransformRule(String rule, TypeName... params) {
    }
}
