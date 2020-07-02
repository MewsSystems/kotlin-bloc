[![Release](https://jitpack.io/v/MewsSystems/kotlin-bloc.svg)](https://jitpack.io/#MewsSystems/kotlin-bloc)
[![](https://github.com/MewsSystems/kotlin-bloc/workflows/Test/badge.svg)](https://github.com/MewsSystems/kotlin-bloc/)

# Kotlin BLoC

BLoC pattern implementation for Kotlin/Android. Inspired by [bloc](https://bloclibrary.dev/) library for Flutter.

## How to use

### Add the JitPack repository

Add it in your root `build.gradle`:

#### Groovy:

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Kotlin

```kotlin
allprojects {
    repositories {
        // ...
        maven(url = "https://jitpack.io")
    }
}
```

### Add the dependency

Add it to your app `build.gradle`:

#### Groovy

```groovy
dependencies {
    implementation 'com.github.MewsSystems.kotlin-bloc:core:[VERSION]' // base functionality
    implementation 'com.github.MewsSystems.kotlin-bloc:android:[VERSION]' // Android-specific things (includes core as well)

    testImplementation 'com.github.MewsSystems.kotlin-bloc:testing:[VERSION]' // for test helpers
}
```

#### Kotlin

```kotlin
dependencies {
    implementation("com.github.MewsSystems.kotlin-bloc:core:[VERSION]") // base functionality
    implementation("com.github.MewsSystems.kotlin-bloc:android:[VERSION]") // Android-specific things (includes core as well)
    
    testImplementation("com.github.MewsSystems.kotlin-bloc:testing:[VERSION]") // for test helpers
}
```

## What is BLoC

BLoC (**B**usiness **Lo**gic **C**omponent) is a design pattern for state management that is built around simple yet powerful idea: BLoC is a component that takes events as an input and produces states as an output. From the very high level it can be viewed as a function mapping sequence of events to sequence of states.
