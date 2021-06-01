import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    maven
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-native-mt")

    implementation(project(":core"))

    testImplementation("junit", "junit", "4.12")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf("-module-name", "${project.group}.${project.name}")
}

repositories {
    mavenCentral()
}
