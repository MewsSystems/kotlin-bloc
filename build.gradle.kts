allprojects {
    group = "com.mews.android.bloc"
    version = "0.1.0"

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
        classpath("com.android.tools.build:gradle:3.6.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}
