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
    val kotlinVersion = "1.3.72"

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}
