plugins {
    id("com.google.protobuf") version "0.9.1"
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "protogen-options"
            version = project.version.toString()

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "ghPackages"
            url = uri("https://maven.pkg.github.com/SuduIDE/protogen")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
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