import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    maven
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0-native-mt")
    testImplementation(project(":testing"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf("-module-name", "${project.group}.${project.name}")
}

repositories {
    mavenCentral()
}
