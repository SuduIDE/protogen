plugins {
    `java-library`
    `test-report-aggregation`
    `maven-publish`
    signing
}

val testReportTask = tasks.register<TestReport>("testReport") {
    dependsOn("testAggregateTestReport")
    destinationDir = file("$buildDir/reports/tests/unit-test/aggregated-results")
    reportOn(subprojects.mapNotNull { it.tasks.findByPath("test") })
}

tasks.test {
    finalizedBy(testReportTask)
}

allprojects {

    group = "io.github.suduide"
    version = System.getenv("RELEASE_VERSION") ?: "0.0.1"

    apply(plugin = "java-library")
    apply(plugin = "test-report-aggregation")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains:annotations:23.0.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    }

    tasks.test {
        useJUnitPlatform()
    }

    project.afterEvaluate {
        if (project.plugins.hasPlugin("maven-publish")) {
            configurePublishedProject(this)
        }
    }
}

fun configurePublishedProject(project: Project) {
    project.java {
        withJavadocJar()
        withSourcesJar()
    }

    project.tasks.withType<Javadoc>() {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }

    project.signing {
        sign(publishing.publications)
    }

    project.publishing {
        publications.withType<MavenPublication>().forEach() { configureMavenPublication(it) }
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
}


fun configureMavenPublication(publication: MavenPublication) {
    publication.pom {
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

    publication.groupId = project.group.toString()
    publication.version = project.version.toString()
    publication.artifact(tasks.findByName("sourcesJar"))
    publication.artifact(tasks.findByName("javadocJar"))
}
