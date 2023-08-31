plugins {
    `java-library`
    `test-report-aggregation`
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
}