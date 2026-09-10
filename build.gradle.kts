import xalitoria.repo.*

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.shadow)
    `maven-publish`
}

dependencies {
    implementation(kotlinx("coroutines-core"))
    implementation(kotlinx("datetime"))

    implementation(project(":color"))

    implementation(libs.kaml)

    compileOnly(libs.paper.api)
    compileOnly(libs.placeholderapi)
}

publishing {
    publications.create<MavenPublication>("plugin") {
        tasks.register("mainJar", Jar::class) {
            archiveClassifier.set("")
            from(sourceSets["main"].output)
        }.get().let(::artifact)
        tasks.register("sourceJar", Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets["main"].allSource)
        }.get().let(::artifact)
    }
}
