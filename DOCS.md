PROTOGEN DOCUMENTATION
=====================

To configure protogen _the protocol buffers language options_ are used. Import `protogen/options.proto` to access to
them.

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

## Message

Message names should follow the Sudu naming convention. i.e:

* Message names are prefixed with Grpc
* Request and response messages also have a suffix Request\Response

Domain object name is simply the name of a message without `Grpc`. You could specify it manually
using `(protogen.message_name)`.

Messages are generated automatically if `(protogen.enable)` option on file is set to true
**and the message name doesn't end with Request\Response**.
To control generation use `(protogen.gen_message)`.

### Unfolding

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
        long pId,
        @NotNull String name,
        @Nullable String email
) {

    @NotNull
    public static Person fromGrpc(@NotNull GrpcPerson proto) {
        return new Person(
                proto.getId().getId(),
                proto.getName(),
                proto.hasEmail() ? proto.getEmail() : null
        );
    }

    @NotNull
    public GrpcPerson toGrpc() {
        GrpcPerson.Builder builder = GrpcPerson.newBuilder();
        builder.setId(GrpcPersonId.newBuilder().setId(pId).build());
        builder.setName(name);
        if (email != null) {
            builder.setEmail(email);
        }
        return builder.build();
    }
}
```

As shown, the `id` field name is taken from the unfolded type. If you want to override it, place the (
protogen.field_name) option in the consuming message.

```protobuf
message GrpcPersonId {
  uint64 pId = 1;
  option (protogen.unfold) = true;
}

message GrpcPerson {
  GrpcPersonId id = 1 [(protogen.field_name) = "personId"];
  string name = 2;
  optional string email = 3;
}
```

### Manually-written domain objects

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

## Enum

Enums are also a kind of domain objects and are generated in the same way as messages. However, due to the limitations
of
the language, the options are called differently.

* `(protogen.gen_enum)` instead of `(protogen.gen_message)`
* `(protogen.enum_name)` instead of `(protogen.message_name)`
* `(protogen.custom_enum)` instead of `(protogen.custom_class)`
* `(protogen.enum_val_name)` instead of `(protogen.field_name)`

### Unused enum values

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

## Service

Generator allows you to generate default sync clients for services. A Default client is a subclass of `BaseGrpcClient`,
that provides default constructors, `protected` `blockingStub` field, and generated methods implementation; If it is not
suitable, you could make a client abstract and inherit from it, or even disable generation.
By default, it prefixes clients with `Default` and replaces `Service` with `Client`.

#### Options

* `(protogen.gen_service)` allows you to manage generation
* `(protogen.service_name)` allows you to manage client name
* `(protogen.make_abstract)` allows you to make a client abstract

## Method

Protogen generates sync methods with non-streaming inputs. If input\output is a domain object, it takes\returns its
corresponding type.
Otherwise, the generator unwraps message fields into parameters\return value. If a response is not a domain
object and consists of more than one field, it's impossible to generate a method. In this case, an exception is thrown.

Take into account, that messages with Response\Request suffixes are not generated as domain objects by default.

Example:

```protobuf
message GrpcScheduleTaskRequest {
  string taskType = 1;
  string clientTag = 2;
  map<string, string> commonParameters = 3;
  repeated GrpcTaskDescriptor taskDescriptor = 4;
}

service ComputeEngineService {
  rpc schedule(GrpcScheduleTaskRequest) returns (GrpcBatchId);
}
```

```java
@NotNull
public String schedule(@NotNull String taskType,@NotNull String clientTag,
@NotNull Map<String, String> commonParameters,
@NotNull List<TaskDescriptor> taskDescriptor){
        GrpcScheduleTaskRequest.Builder requestBuilder=GrpcScheduleTaskRequest.newBuilder();
        requestBuilder.setTaskType(taskType);
        requestBuilder.setClientTag(clientTag);
        requestBuilder.putAllCommonParameters(commonParameters);
        requestBuilder.addAllTaskDescriptor(taskDescriptor.stream()
        .map(i->i.toGrpc())
        .collect(Collectors.toList()));
        GrpcScheduleTaskRequest request=requestBuilder.build();
        return blockingStub.schedule(request).getUuid();
        }
```

#### Options

to set an option to a method use such syntax:

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