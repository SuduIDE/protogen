plugins {
    `maven-publish`
    signing
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
                description = "protoc plugin of protogen"
                name = "protoc-gen-protogen"
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
            artifactId = "protoc-gen-protogen"
            version = project.version.toString()
            artifact(tasks.findByName("sourcesJar"))
            artifact(tasks.findByName("javadocJar"))
            artifact(tasks.shadowJar) {
                classifier = "jvm"
            }
        }
    }
}

tasks.shadowJar {
    archiveClassifier = "jvm"
    manifest.attributes["Main-Class"] = "org.sudu.protogen.protoc.Main"
}

dependencies {
    implementation(project(":options"))
    implementation(project(":javapoet"))
    implementation("com.google.protobuf:protobuf-java:3.21.9")

    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("commons-io:commons-io:2.13.0")
}