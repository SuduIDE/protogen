@Suppress("DSL_SCOPE_VIOLATION") // Remove once KTIJ-19369 is fixed // to remove IDEA error on alias
plugins {
    java
    id("com.google.protobuf") version "0.9.1"
}

repositories {
    mavenCentral()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.9"
    }
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.21.9")
}