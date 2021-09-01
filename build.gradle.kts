allprojects {
    group = "com.github.MewsSystems.kotlin-bloc"

    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    val kotlinVersion = "1.5.30"

    dependencies {
        classpath("com.android.tools.build:gradle:3.6.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}
