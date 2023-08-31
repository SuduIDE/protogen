plugins {
    id("com.google.protobuf") version "0.9.1"
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                description = "Options for protogen tool. Use it as protogen scope dependency"
                name = "protogen-options"
            }
            artifactId = "protogen-options"
            artifact(tasks.jar)
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.9"
    }
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.21.9")
}