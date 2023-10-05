package org.sudu.protogen.descriptors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import protogen.Options;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public enum RepeatedContainer implements Descriptor {

    LIST(ClassName.get(List.class)) {
        @Override
        public CodeBlock getCollectorExpr() {
            return CodeBlock.of(".toList()");
        }

        @Override
        public CodeBlock getToStreamExpr(CodeBlock thisRef) {
            return CodeBlock.of("$L.stream()", thisRef);
        }

        @Override
        public CodeBlock getEmptyOne() {
            return CodeBlock.of("new $T<>()", ArrayList.class);
        }
    },

    SET(ClassName.get(Set.class)) {
        @Override
        public CodeBlock getCollectorExpr() {
            return CodeBlock.of(".collect($T.toSet())", Collectors.class);
        }

        @Override
        public CodeBlock getToStreamExpr(CodeBlock thisRef) {
            return CodeBlock.of("$L.stream()", thisRef);
        }

        @Override
        public CodeBlock getEmptyOne() {
            return CodeBlock.of("new $T<>()", HashSet.class);
        }
    },

    ITERATOR(ClassName.get(Iterator.class)) {
        @Override
        public CodeBlock getCollectorExpr() {
            return CodeBlock.of(".iterator()");
        }

        @Override
        public CodeBlock getToStreamExpr(CodeBlock thisRef) {
            return CodeBlock.of(
                    "$T.stream($T.spliteratorUnknownSize($L, 0), false)",
                    StreamSupport.class,
                    Spliterators.class,
                    thisRef
            );
        }

        @Override
        public CodeBlock convertListToInstance(CodeBlock thisRef) {
            return CodeBlock.of("$L.iterator()", thisRef);
        }

        @Override
        public CodeBlock convertInstanceToIterable(CodeBlock thisRef) {
            return CodeBlock.of("() -> $L", thisRef);
        }

        @Override
        public CodeBlock getEmptyOne() {
            return CodeBlock.of("$T.emptyIterator()", Collections.class);
        }
    },

    STREAM(ClassName.get(Stream.class)) {
        @Override
        public CodeBlock getCollectorExpr() {
            return CodeBlock.of("");
        }

        @Override
        public CodeBlock getToStreamExpr(CodeBlock thisRef) {
            return thisRef;
        }

        @Override
        public CodeBlock convertListToInstance(CodeBlock thisRef) {
            return CodeBlock.of("$L.stream()", thisRef);
        }

        @Override
        public CodeBlock convertInstanceToIterable(CodeBlock thisRef) {
            return CodeBlock.of("$L.toList()", thisRef);
        }

        @Override
        public CodeBlock getEmptyOne() {
            return CodeBlock.of("$T.empty()", Stream.class);
        }
    };

    private final ClassName typeName;

    RepeatedContainer(ClassName typeName) {
        this.typeName = typeName;
    }

    public static RepeatedContainer fromGrpc(Options.RepeatedContainer proto) {
        return switch (proto) {
            case UNRECOGNIZED -> throw new IllegalArgumentException();
            case LIST -> RepeatedContainer.LIST;
            case SET -> RepeatedContainer.SET;
            case ITERATOR -> RepeatedContainer.ITERATOR;
            case STREAM -> RepeatedContainer.STREAM;
        };
    }

    /**
     * Builds an expressions transforming an instance of the container into stream
     */
    public abstract CodeBlock getToStreamExpr(CodeBlock thisRef);

    /**
     * Builds an expressions collecting a stream into the container
     */
    public abstract CodeBlock getCollectorExpr();

    public abstract CodeBlock getEmptyOne();

    /**
     * Builds an expression converting a java.Util.List instance into the container
     * Used to build .getSomeList().... expressions
     * Necessary, because getToStreamExpr consumes container instance, but protobuf-message getter returns List
     */
    public CodeBlock convertListToInstance(CodeBlock thisRef) {
        return thisRef;
    }

    /*
     * Builds an expression converting the container to Iterable<>
     * Used to build .addSome(..) calls in protobuf-generated builders
     * It's necessary, because there is no way to directly pass stream or iterator as a parameter
     */
    public CodeBlock convertInstanceToIterable(CodeBlock thisRef) {
        return thisRef;
    }

    public ClassName getTypeName() {
        return typeName;
    }
}
