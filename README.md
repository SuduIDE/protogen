PROTOGEN
========

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

### Getting Started

At first, you need to set `(protogen.enable)` option on your file to say the tool you want to generate it

```protobuf
import "protogen/options.proto";

option (protogen.enable) = true;
```

If the option is true, Protogen automatically generates domain objects and default clients for the whole content.

Message name should follow the Sudu naming convention. i.e:

* Message names are prefixed with Grpc
* Request and response messages are suffixed with Request\Response

As response messages are not generated automatically, you need to handle them on your own.
If there is a method returning a not-generated message with more than one field, the build fails.
Use `(protogen.gen_message)` to configure generation. (See more in the **method** section of [DOCS.md](DOCS.md))

```protobuf
message GrpcGetPersonResponse {
  GrpcPerson person = 1;
  int32 field2 = 2;
  option (protogen.gen_message) = true;
}
```

---

See [DOCS.md](DOCS.md) 