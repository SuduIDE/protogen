plugins {
    `maven-publish`
    signing
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                description = "protoc plugin of protogen"
                name = "protogen-engine"
            }
            artifactId = "protogen-engine"
            artifact(tasks.shadowJar) {
                classifier = "jvm"
            }
        }
    }
}

dependencies {
    implementation(project(":options"))
    implementation(project(":javapoet"))
    implementation("com.google.protobuf:protobuf-java:3.21.9")

    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("commons-io:commons-io:2.13.0")
}