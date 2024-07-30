// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
//        added for firebase buildscript{dependencies{firebase}}
        classpath(libs.google.services)
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    //Hilt
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.25-1.0.20" apply false
}