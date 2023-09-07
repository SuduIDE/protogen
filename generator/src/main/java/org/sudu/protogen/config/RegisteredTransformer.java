package org.sudu.protogen.config;

import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public record RegisteredTransformer(
        String protoType,
        String javaClass,
        TransformRule protoToJava,
        TransformRule javaToProto
) {

    @NotNull
    public static List<RegisteredTransformer> defaultTransformers() {
        RegisteredTransformer emptyTransformer = new RegisteredTransformer(
                "google.protobuf.Empty",
                "void",
                new TransformRule("$L"),
                new TransformRule("")
        );
        RegisteredTransformer timestampTransformer = new RegisteredTransformer(
                "google.protobuf.Timestamp",
                "java.time.Instant",
                new TransformRule("$T.ofEpochSecond($L.getSeconds(), $L.getNanos())"),
                new TransformRule(".setSeconds($L.getEpochSecond()).setNanos($L.getNano())")
        );
        RegisteredTransformer durationTransformer = new RegisteredTransformer(
                "google.protobuf.Duration",
                "java.time.Duration",
                new TransformRule("$T.ofSeconds($L.getSeconds(), $L.getNanos())"),
                new TransformRule(".setSeconds($L.getSeconds()).setNanos($L.getNano())")
        );
        RegisteredTransformer byteStringTransformer = new RegisteredTransformer(
                "google.protobuf.BytesValue",
                "com.google.protobuf.ByteString",
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
                        makeWrapperTransformer("google.protobuf.DoubleValue", "double"),
                        makeWrapperTransformer("google.protobuf.FloatValue", "float"),
                        makeWrapperTransformer("google.protobuf.Int64Value", "long"),
                        makeWrapperTransformer("google.protobuf.UInt64Value", "long"),
                        makeWrapperTransformer("google.protobuf.Int32Value", "int"),
                        makeWrapperTransformer("google.protobuf.UInt32Value", "int"),
                        makeWrapperTransformer("google.protobuf.BoolValue", "bool"),
                        makeWrapperTransformer("google.protobuf.StringValue", "String")
                )
        ).toList();
    }

    @NotNull
    private static RegisteredTransformer makeWrapperTransformer(String wrapperType, String javaClass) {
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
