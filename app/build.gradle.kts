plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.mat.mindpet"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.mat.mindpet"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout.v220)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.viewpager2)
    implementation(libs.material.calendarview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.dagger:hilt-android:2.46.1")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("com.google.guava:guava:31.1-android")
    annotationProcessor("com.google.dagger:hilt-compiler:2.46.1")
}