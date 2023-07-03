plugins {
    kotlin("jvm") version "1.8.22"
    alias(libs.plugins.kotlin.serialization)
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