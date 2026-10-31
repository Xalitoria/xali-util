import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import mc.xalitoria.gradle.repo.*

plugins {
    kotlin("jvm")
    id("mc.xalitoria.conventions")
    kotlin("plugin.serialization")
    `maven-publish`
}

group = "mc.xalitoria"
version = "0.1.0"

dependencies {
    api(kotlinx("coroutines-core"))
    api(kotlinx("serialization-core"))
    api(kotlinx("serialization-json"))
    api(kotlinx("datetime"))
    api(libs.xalicraft.color)
    api(libs.kaml)
    api(libs.bundles.adventure)

    compileOnly(libs.paper.api)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
    disableAutoTargetJvm()
}

kotlin {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
    jvmToolchain(11)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven("https://maven.xali.host/releases") {
            credentials {
                username = System.getenv("MAVEN_USER") ?: project.findProperty("maven.user") as? String
                password = System.getenv("MAVEN_TOKEN") ?: project.findProperty("maven.token") as? String
            }
        }
    }
}