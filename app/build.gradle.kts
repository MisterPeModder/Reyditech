@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "eu.epitech.reyditech"
    compileSdk = 33

    testCoverage {
        jacocoVersion = "0.8.8"
    }

    defaultConfig {
        applicationId = "eu.epitech.reyditech"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard/proguard-rules.pro",
                "proguard/retrofit2.pro",
            )
        }

        debug {
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material Design 2 Compose
    implementation("androidx.compose.material:material")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Material Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Window Size Utils
    implementation("androidx.compose.material3:material3-window-size-class")

    // Compose Integration with activities
    implementation("androidx.activity:activity-compose:1.6.1")
    // Compose Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Key-Value Based Persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Oauth2 Client
    implementation("net.openid:appauth:0.11.1")

    // Retrofit - HTTP Client
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Moshi - JSON Parser
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.moshi:moshi-adapters:1.13.0")

    // Retrofit with Moshi Converter
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Mocks the Android SDK for testing
    testImplementation("org.robolectric:robolectric:4.9")

    // Dynamic Paging
    implementation("androidx.paging:paging-runtime:3.1.1")
    testImplementation("androidx.paging:paging-common:3.1.1")
    implementation("androidx.paging:paging-compose:1.0.0-alpha18")

    // Placeholder widgets
    implementation("com.google.accompanist:accompanist-placeholder-material:0.29.1-alpha")
}
