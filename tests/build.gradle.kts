import com.google.protobuf.gradle.id

plugins {
    java
    id("com.google.protobuf") version "0.9.1"
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
            path = "${project(":generator").buildDir.absolutePath}/libs/generator-${version}-jvm.jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.dependsOn(project(":generator").tasks.findByName("shadowJar"))
            it.plugins {
                id("grpc")
                id("protogen") {
                    // protoc utilizes colons (the ":" character) as option separators,
                    // so windows-paths broke options list. To overcome it I suggest to replace it with "*"
                    option("config=$projectDir/src/test/proto/protogen/".replace(":", "*"))
                }
            }
        }
    }
}

dependencies {
    protobuf(project(":generator"))
    implementation(project(":options"))
    implementation("io.grpc:grpc-protobuf:1.51.0")
    implementation("io.grpc:grpc-stub:1.51.0")
    implementation("io.grpc:grpc-services:1.51.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("com.google.protobuf:protobuf-java:3.21.9")
    testImplementation("org.assertj:assertj-core:3.24.2")
}