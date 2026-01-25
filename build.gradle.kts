plugins {
    java
    application
}

group = "ru.mokujin"
version = "1.0"

application {
    mainClass.set("ru.mokujin.filter.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.beust:jcommander:1.82")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}