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
    project.tasks.withType<Javadoc>() {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }

    project.apply(plugin = "signing")

    project.publishing {
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
                val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
                credentials {
                    username = System.getenv("SONATYPE_USERNAME")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }
}