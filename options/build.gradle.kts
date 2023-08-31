plugins {
    id("com.google.protobuf") version "0.9.1"
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                url = "https://github.com/SuduIDE/protogen"
                organization {
                    name = "com.github.SuduIDE"
                    url = "https://github.com/SuduIDE"
                }
                issueManagement {
                    system = "GitHub"
                    url = "https://github.com/SuduIDE/protogen/issues"
                }
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                scm {
                    url = "https://github.com/SuduIDE/protogen"
                    connection = "scm:https://github.com/SuduIDE/protogen.git"
                    developerConnection = "scm:git://github.com/SuduIDE/protogen.git"
                }
                developers {
                    developer {
                        id = "Duzhinsky"
                        name = "Dmitrii Duzhinskii"
                        email = "dduzhinsky@ya.ru"
                    }
                }
            }

            groupId = project.group.toString()
            version = project.version.toString()
            artifact(tasks.findByName("sourcesJar"))
            artifact(tasks.findByName("javadocJar"))

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