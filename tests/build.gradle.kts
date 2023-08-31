import com.google.protobuf.gradle.id

@Suppress("DSL_SCOPE_VIOLATION") // Remove once KTIJ-19369 is fixed
plugins {
    java
    id("com.google.protobuf") version "0.9.1"
}

/**
 * As protobuf plugin accepts either path we can't determine or string artifact, there is a need to connect
 * string-artifacts with project(":protogen:generator")
 * i.e. `artifact = project(":protogen:generator")` doesn't work without that configuration
 */
configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("org.sudu:protogen-generator")).using(project(":generator"))
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.9"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.51.0"
        }
        id("protogen") {
            artifact = "org.sudu:protogen-generator:+:jdk@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            run {
                task.dependsOn(":generator:build")
                task.plugins { id("grpc"); id("protogen") }
            }
        }
    }
}

dependencies {
    implementation("io.grpc:grpc-protobuf:1.51.0")
    implementation("io.grpc:grpc-stub:1.51.0")
    implementation("io.grpc:grpc-services:1.51.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("com.google.protobuf:protobuf-java:3.21.9")
    protobuf(project(":options"))
    testImplementation("org.assertj:assertj-core:3.24.2")
}