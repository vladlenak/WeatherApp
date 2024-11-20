plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "t.me.octopusapps.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "t.me.octopusapps.weatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = property("apikey")?.toString()
            ?: error("Add apikey into gradle.properties")
        buildConfigField("String", "API_KEY", "\"${apiKey}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Navigation Component
    implementation(libs.navigation.fragment.ktx)

    // GMS
    implementation (libs.play.services.location)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)

    // Glide
    implementation (libs.glide)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}