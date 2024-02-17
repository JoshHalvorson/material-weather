buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.0-alpha10")
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.0-alpha10" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.library") version "8.3.0-alpha10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.8.21"
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
}