import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.versions)
    `maven-publish`
    `java-library`
    signing
}

group = "uk.dioxic.kotlinx"
version = "0.0.1"

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

kotlin {
    jvmToolchain(11)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.named<KotlinCompilationTask<*>>("compileTestKotlin").configure {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dioxic/kotlinx-serialization-bson")
            credentials {
                val ghUsername: String? by project
                val ghToken: String? by project
                username = ghUsername
                password = ghToken
            }
        }
    }
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
    val local: String? by project
    if (local != "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications["mavenJava"])
}