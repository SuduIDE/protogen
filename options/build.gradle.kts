plugins {
    id("com.google.protobuf") version "0.9.1"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.9"
    }
}

tasks.jar {
    archiveClassifier = "options"
}

dependencies {
    implementation("io.grpc:grpc-api:1.51.0")
    implementation("com.google.protobuf:protobuf-java:3.21.9")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
