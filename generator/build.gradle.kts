plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":options"))
    implementation(project(":javapoet"))
    implementation("com.google.protobuf:protobuf-java:3.21.9")

    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("commons-io:commons-io:2.13.0")
}

tasks.jar {
    dependsOn(":options:build")
    dependsOn(":javapoet:build")
    archiveBaseName.set("protogen-generator")
    manifest {
        // To debug uncomment the line below, and run generator. It'll generate a descriptor_dump file in generated folder
        // Then comment the line back and run org.sudu.protogen.protoc.Main from IDEA with descriptor_dump absolute path as a parameter
        // attributes.put("Main-Class", "org.sudu.protogen.protoc.plugin.dump.DumpGenerator")
        attributes.put("Main-Class", "org.sudu.protogen.protoc.Main")
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    archiveClassifier.set("jdk")
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory()) it else zipTree(it) }))
    {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
}