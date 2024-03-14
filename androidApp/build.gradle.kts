plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    namespace = "jp.co.soramitsu.xnetworking.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "jp.co.soramitsu.xnetworking.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(mapOf("path" to ":lib:basic")))
    implementation(project(mapOf("path" to ":lib:fearlesswallet")))
    implementation(project(mapOf("path" to ":lib:sorawallet")))
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.ui:ui-tooling:1.6.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.3")
    implementation("androidx.compose.foundation:foundation:1.6.3")
    implementation("androidx.compose.material:material:1.6.3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.ktor:ktor-client-core:2.3.1")

    //test
    implementation("androidx.test.ext:junit:1.1.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.ktor:ktor-client-mock:2.3.1")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.1")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:2.3.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.3")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.work:work-testing:2.9.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}