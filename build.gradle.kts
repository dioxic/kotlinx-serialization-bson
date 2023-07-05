import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
    `java-library`
    signing
}

group = "uk.dioxic.kotlinx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bson)
    implementation(libs.kotlin.serialization.core)
    testImplementation(libs.bundles.kotest)
}

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<KotlinCompile>().forEach {
    it.kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
//                name.set("My Library")
                description.set("Kotlin Serialization for BSON")
                url.set("https://github.com/dioxic/kotlinx-serialization-bson")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("dioxic")
                        name.set("Mark Baker-Munton")
                        email.set("dioxic@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/dioxic/kotlinx-serialization-bson.git")
                    developerConnection.set("scm:git:git@github.com:dioxic/kotlinx-serialization-bson.git")
                    url.set("https://github.com/dioxic/kotlinx-serialization-bson")
                }
            }
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}