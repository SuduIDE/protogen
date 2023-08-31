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

                description = "Options for protogen tool. Use it as protogen scope dependency"
                name = "protogen-options"
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
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
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