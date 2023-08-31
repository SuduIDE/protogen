tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("com.google.testing.compile:compile-testing:0.21.0")
    testImplementation("com.google.jimfs:jimfs:1.3.0")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.eclipse.jdt.core.compiler:ecj:4.6.1")
}