package org.sudu.protogen;

import com.squareup.javapoet.ClassName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sudu.protogen.config.Configuration;
import org.sudu.protogen.config.RegisteredTransformer;
import org.sudu.protogen.generator.TypeTable;
import org.sudu.protogen.protobuf.EnumMock;
import org.sudu.protogen.protobuf.FileMock;
import org.sudu.protogen.protobuf.MessageMock;

import java.util.List;
import java.util.Optional;

public class TypeTableUnitTest {

    /*  DEFAULT:
        a.proto:

        package test;

        option java_multiple_files=true;

        message A {
            ...
        }

        message B {
            ...
            message C {
                ...
            }
        }

        enum D {
            ...
        }
    */

    FileMock aFile;
    MessageMock aMessage;
    MessageMock bMessage;
    MessageMock cMessage;
    EnumMock dEnum;
    Configuration config;

    @BeforeEach
    public void prepareMocks() {
        config = new Configuration(
                name -> name,
                4,
                ClassName.get("org.jetbrains.annotations", "Nullable"),
                ClassName.get("org.jetbrains.annotations", "NotNull"),
                RegisteredTransformer.defaultTransformers()
        );
        aFile = new FileMock(
                "a.proto",
                "test",
                List.of(),
                List.of(),
                Optional.of(true),
                Optional.empty(),
                Optional.empty(),
                Optional.of(true),
                Optional.empty()
        );
        aMessage = MessageMock.builder()
                .name("A")
                .fullName("test.A")
                .containingFile(aFile)
                .build();
        bMessage = MessageMock.builder()
                .name("B")
                .fullName("test.B")
                .containingFile(aFile)
                .build();
        cMessage = MessageMock.builder()
                .name("C")
                .fullName("test.B.C")
                .containingFile(aFile)
                .containingType(bMessage)
                .build();
        bMessage.setNested(List.of(cMessage));
        dEnum = EnumMock.builder()
                .name("D")
                .fullName("test.D")
                .containingFile(aFile)
                .build();
        aFile.nested = List.of(aMessage, bMessage, dEnum);
    }

    @Test
    public void javaPackageTest() {

        aFile.javaPackageOption = Optional.empty();
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test", "B.C"));

        aFile.javaPackageOption = Optional.of("tesssT");
        generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(bMessage), ClassName.get("tesssT", "B"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("tesssT", "B.C"));
        protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("tesssT", "B.C"));
        Assertions.assertEquals(protoTable.getType(dEnum), ClassName.get("tesssT", "D"));

    }

    @Test
    public void javaMultipleFilesTest() {
        aFile.javaMultipleFilesOption = Optional.empty();
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test.AOuterClass", "A"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test.AOuterClass", "B.C"));
        aFile.name = "h.proto";
        protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test.H", "A"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test.H", "B.C"));

        aFile.javaMultipleFilesOption = Optional.of(true);
        generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test", "B.C"));

    }

    @Test
    public void javaOuterClassnameTest() {
        aFile.javaMultipleFilesOption = Optional.empty();
        aFile.javaOuterClassnameOption = Optional.of("OUTER");
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test.OUTER", "A"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test.OUTER", "B.C"));
        aFile.name = "h.proto";
        protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test.OUTER", "A"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test.OUTER", "B.C"));


        aFile.javaMultipleFilesOption = Optional.of(true);
        generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test", "B.C"));
    }

    @Test
    public void nestedTypeTest() {
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(generatedTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        Assertions.assertEquals(generatedTable.getType(dEnum), ClassName.get("test", "D"));
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(protoTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test", "B.C"));
        Assertions.assertEquals(protoTable.getType(dEnum), ClassName.get("test", "D"));
    }

    /**
     * All types are discovered. None of other types are discovered.
     */
    @Test
    public void discoverTest() {
        aFile.javaPackageOption = Optional.empty();
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(generatedTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("test", "B.C"));
        Assertions.assertEquals(generatedTable.getType(dEnum), ClassName.get("test", "D"));
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("test", "A"));
        Assertions.assertEquals(protoTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("test", "B.C"));
        Assertions.assertEquals(protoTable.getType(dEnum), ClassName.get("test", "D"));

        MessageMock notDiscovered = MessageMock.builder()
                .name("NotDiscovered")
                .fullName("test.NotDiscovered")
                .containingFile(aFile)
                .build();
        Assertions.assertThrows(ProtogenException.class, () -> protoTable.getType(notDiscovered));
        Assertions.assertThrows(ProtogenException.class, () -> generatedTable.getType(notDiscovered));
    }

    @Test
    public void protogenPackageTest() {
        aFile.javaPackageOption = Optional.of("javapack");
        aFile.protogenPackageOption = Optional.of("generatedpack");
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        Assertions.assertEquals(generatedTable.getType(aMessage), ClassName.get("generatedpack", "A"));
        Assertions.assertEquals(generatedTable.getType(bMessage), ClassName.get("generatedpack", "B"));
        Assertions.assertEquals(generatedTable.getType(cMessage), ClassName.get("generatedpack", "B.C"));
        Assertions.assertEquals(generatedTable.getType(dEnum), ClassName.get("generatedpack", "D"));
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("javapack", "A"));
        Assertions.assertEquals(protoTable.getType(aMessage), ClassName.get("javapack", "A"));
        Assertions.assertEquals(protoTable.getType(bMessage), ClassName.get("javapack", "B"));
        Assertions.assertEquals(protoTable.getType(cMessage), ClassName.get("javapack", "B.C"));
        Assertions.assertEquals(protoTable.getType(dEnum), ClassName.get("javapack", "D"));
    }

    @Test
    public void testCustomClass() {
        bMessage.setCustomClassNameOption(Optional.of("test.BCustom"));
        TypeTable generatedTable = TypeTable.makeDomainTypeTable(List.of(aFile), config);
        TypeTable protoTable = TypeTable.makeProtoTypeTable(List.of(aFile), config);

        Assertions.assertEquals(protoTable.getType(bMessage), ClassName.get("test", "B"));
        Assertions.assertEquals(generatedTable.getType(bMessage), ClassName.get("test", "BCustom"));
    }
}
