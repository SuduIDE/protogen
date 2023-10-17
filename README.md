![options maven](https://maven-badges.herokuapp.com/maven-central/io.github.suduide/protogen-options/badge.svg)
![build status](https://github.com/SuduIDE/protogen/actions/workflows/build.yml/badge.svg)

## Table of contents

1. [About](#about)
2. [Getting started](#getting-started)
    1. [Installation](#installation)
    2. [First steps](#first-steps)
3. [Options overview](#options-overview)
    1. [File and package](#file-and-package)
    2. [Message](#message)
    3. [Enum](#enum)
    4. [Service](#service)
    5. [Method](#method)
4. [Contribution](#contribution)
    1. [Design Guide](#design-guide)

## About

Protogen is a tool to generate high-level domain objects for Protobuf messages.
Moreover, it generates human-readable base clients.

Suppose you have the following model:

```protobuf
message GrpcPerson {
  uint64 id = 1;
  string name = 2;
  optional string email = 3;
}
```

It generates the following code:

```java
public record Person(
        long id,
        @NotNull String name,
        @Nullable String email
) {

    @NotNull
    public static Person fromGrpc(@NotNull GrpcPerson proto) {
        return new Person(
                proto.getId(),
                proto.getName(),
                proto.hasEmail() ? proto.getEmail() : null
        );
    }

    @NotNull
    public GrpcPerson toGrpc() {
        GrpcPerson.Builder builder = GrpcPerson.newBuilder();
        builder.setId(id);
        builder.setName(name);
        if (email != null) {
            builder.setEmail(email);
        }
        return builder.build();
    }
}
```

## Getting Started

### Installation

The tool is published at the Maven central repository. Use `io.github.suduide:protoc-gen-protogen:latest:jvm@jar`
artifact to specify the plugin executable
and `io.github.suduide:protoc-gen-protogen:latest:options` to supply options.

<details>
  <summary>Example of configuration</summary>

build.gradle.kts:

  ```kotlin
  protobuf {
    protoc {
        // ...
    }
    plugins {
        // ...
        id("protogen") {
            artifact = "io.github.suduide:protoc-gen-protogen:1.0.11:jvm@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                // ...
                id("protogen")
            }
        }
    }
}

dependencies {
    // ...
    protobuf("io.github.suduide:protoc-gen-protogen:1.0.11:options") // or implementation
}
  ```

</details>

### First steps

At first, you need to set `(protogen.enable)` option on your file to say the tool you want to generate it

```protobuf
import "protogen/options.proto";

option (protogen.enable) = true;
```

If the option is true, Protogen automatically generates domain objects and default clients for the whole content.

Message name should follow the Sudu naming convention. i.e:

* Message names are prefixed with Grpc
* Request and response messages are suffixed with Request\Response

As `*Response*` and `*Request*` messages are not generated automatically, you need to handle them on your own.
If there is a method returning a not-generated message with more than one field, the build fails.
Use `(protogen.gen_message)` to configure generation. 

```protobuf
message GrpcGetPersonResponse {
  GrpcPerson person = 1;
  int32 field2 = 2;
  option (protogen.gen_message) = true;
}
```

---

Options overview
=====================

To configure protogen _options_ are used. Import `protogen/options.proto` to access them.

```protobuf
import "protogen/options.proto";
```

## File and package

To apply protogen on the whole file content use the `(protogen.enable)` option.

```protobuf
option (protogen.enable) = true;
```

By default, it places generated classes in the package specified by the `package` statement, omitting `.grpc`.
If it's not the expected behavior, and you don't want to rename it, there is the `(protogen.pkg)` option.

```protobuf
package org.sudu.index.cpp.grpc;
option (protogen.pkg) = "org.sudu.api.common.cpp";
```

* If you don't want to see `@NotNull` annotations in generated code for models from a file, you could use `option (protogen.disable_notnull) = true;`

## Message

Message names should follow the Sudu naming convention. i.e:

* Message names are prefixed with Grpc
* Request and response messages also have a suffix Request\Response

Domain object name is simply the name of a message without `Grpc`. You could specify it manually
using `(protogen.message_name)`.

Messages are generated automatically if `(protogen.enable)` option on file is set to true
**and the message name doesn't contain Request\Response**.
To control generation use `(protogen.gen_message)`.


<details>
<summary>Unfolding one-field messages</summary>

Sometimes we utilise wrapper-messages containing only one field. Usually, it's `GrpcXId` and `GrpcXIdBatch`.
Such messages are not domain, so, to avoid their generation, _unfolding_ is used.

_Unfolding_ is a mechanism to unwrap one-field messages. Set `(protogen.unfold) = true` to
activate it. Domain objects for such messages are not generated automatically.
However, you could set `(protogen.gen_message) = true` if you want to.

Proto:

```protobuf
message GrpcPersonId {
  uint64 pId = 1;
  option (protogen.unfold) = true;
}

message GrpcPerson {
  GrpcPersonId id = 1;
  string name = 2;
  optional string email = 3;
}
```

```java
public record Person(
        long id,
        @NotNull String name,
        @Nullable String email
) {

    @NotNull
    public static Person fromGrpc(@NotNull GrpcPerson proto) {
        return new Person(
                proto.getId().getPId(),
                proto.getName(),
                proto.hasEmail() ? proto.getEmail() : null
        );
    }

    @NotNull
    public GrpcPerson toGrpc() {
        GrpcPerson.Builder builder = GrpcPerson.newBuilder();
        builder.setId(GrpcPersonId.newBuilder().sePtId(id).build());
        builder.setName(name);
        if (email != null) {
            builder.setEmail(email);
        }
        return builder.build();
    }
}
```
</details>

<details>
<summary>Using manually-written domain objects</summary>

Sometimes, the expected code is out-of-scope of the generator. In such cases, generation should be disabled.
However, if the option `(protogen.gen_message)` is set to false, it wouldn't be possible to cross-reference a domain
class
in generated code.
So, to provide the tool with such information the `(protogen.custom_class)` option is used. Specify the
the fully-qualified name of the manually-written class as a parameter.

```protobuf
message GrpcBatchInfo {
  GrpcBatchId batch = 1;
  map<string, string> commonParameters = 2;
  repeated GrpcTaskInfo task = 3;
  string clientTag = 4;
  string taskType = 5;
  optional string context = 6;
  option (protogen.custom_class) = "org.sudu.api.computeengine.BatchInfo";
}
```
</details>

<details>
<summary>Implement Comparable</summary>

This is done by providing a reference to an existing `public static Comparator`. For example:
```java
public class ConsoleComparators {
    public static final Comparator<InstanceId> INSTANCE_ID =
            Comparator.comparing(InstanceId::service).thenComparing(InstanceId::name);
}
```

Then add `option (protogen.message_comparator)`
```protobuf
message GrpcInstanceId {
  string service = 1;
  string name = 2;
  option (protogen.message_comparator) = "org.sudu.api.console.types.ConsoleComparators.INSTANCE_ID";
}
```
Which leads to:
```java
public record InstanceId(
    String service,
    String name
) implements Comparable<InstanceId> {
    
    @Override
    public int compareTo(InstanceId rhs) {
        return org.sudu.api.console.types.ConsoleComparators.INSTANCE_ID.compare(this, rhs);
    }
    // ...
}
```

</details>

<details>
<summary>Specifying the TOPIC field</summary>

Topic field for a message could be specified using `option (protogen.topic)`
Ex:
```protobuf
message GrpcSomeEvent {
  repeated string ids = 1;
  option (protogen.topic) = "SOME_TOPIC";
}
```
```java
public record SomeEvent(@NotNull List<String> ids) {
    
    public static final String TOPIC = "SOME_TOPIC";
    //...
}
```
</details>


## Enum

Enums are also a kind of domain objects and are generated in the same way as messages. However, due to the limitations
of
the language, the options are called differently.

* `(protogen.gen_enum)` instead of `(protogen.gen_message)`
* `(protogen.enum_name)` instead of `(protogen.message_name)`
* `(protogen.custom_enum)` instead of `(protogen.custom_class)`
* `(protogen.enum_val_name)` instead of `(protogen.field_name)`

<details>
<summary>Unused enum values</summary>

Also, there is an enum-specific option that allows you to mark some values as unused.
Domain object doesn't contain such values and throws an exception consuming it in `fromGrpc`

```protobuf
enum GrpcA {
  A = 0;
  B = 1 [(protogen.unused_enum_val) = true];
}
```

```java
enum A {
    A;

    public GrpcA toGrpc() {
        switch (this) {
            case A:
                return GrpcA.A;
        }
        throw new IllegalStateException();
    }

    public static A fromGrpc(GrpcA grpc) {
        switch (grpc) {
            case A:
                return A.A;
            case B:
                throw new IllegalArgumentException("B value is marked as unused");
        }
        throw new IllegalArgumentException("Enum value is not recognized");
    }
}
```
</details>

## Service

Generator allows you to generate default clients and base services. 

#### Options

* `(protogen.gen_service)` allows you to manage generation
* `(protogen.service_name)` allows you to manage client and service name
* `(protogen.make_abstract)` allows you to make a client abstract

## Method

To set an option to a method use such syntax:

```protobuf
rpc schedule(GrpcScheduleTaskRequest) returns (GrpcBatchId) {
  option (protogen.gen_method) = false;
}
```

* `(protogen.gen_method)` manages generation
* `(protogen.unfold_request)` marks that if a request is a domain object, its fields should be unwrapped into method
  parameters likewise it isn't a domain object.
* `(protogen.make_nullable)` annotates method with `@Nullable` and wraps response into `nullifyIfNotFound` method
  of `BaseGrpcClient`
* `(protogen.stream_to_container)` if the output is streaming collects it into a specified container

---

CONTRIBUTION
============

The tool is made for the SuduIDE internal requirements, so it has some specifics. If you want to add some functionality
keep backward compatibility with the current implementation. Feel free to discuss your enhancement in issues.

## Design Guide

                    <=== 2. Send model ===                 <== 1. Request (AST) == 
        Javapoet                             Generator                               Protoc  
                    = 3. Generated code =>                 = 4. Generated files => 

Protogen has 3 main parts which work together to produce code. It's the generator itself, protoc and javapoet.

* Protoc is the protobuf language compiler that allows to plugin at the code generation phase
* Javapoet is the tool that allows to generate **human-readable** code by its model with graceful API
* The generator transforms protobuf AST to domain classes and clients

**Protoc's plugins are executables** that consume input from _stdin_ and produce output into _stdout_. So there is
no need to make the generator an _implementation_ dependency, just build it before the protoc runs.

### Modules

* **Options** are made to define the external options of the plugin. They are separated because they have to be
  a run-time dependency, unlike the generator which is a compile-time dependency.
* **Javapoet** is a fork of [square/javapoet](https://github.com/square/javapoet) supporting records
* **Generator** - plugin and model assembly
* **Tests** As the plugin is an executable its external tests were separated to ask gradle build generator first
