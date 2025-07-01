plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.21"
    kotlin("kapt")
}

android {
    namespace = "com.asm.taken"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.asm.taken"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {

    val coreKtxVersion = "1.13.1"
    val roomVersion = "2.6.1"
    val hiltVersion = "2.51.1"
    val navVersion = "2.9.0"
    val kotlinSerializer = "1.7.3"
    val firebaseVersion = "33.1.0"
    val gsonVersion = "2.11.0"
    val credentials = "1.2.2"
    val googleId = "1.1.0"
    val retrofitVersion = "2.11.0"
    val coilVersion = "2.6.0"
    val lifecycleRuntimeVersion = "2.8.2"
    val activityComposeVersion = "1.9.0"
    val composeBomVersion = "2024.06.00"
    val iconsExtendedVersion = "1.6.8"
    val kotlinCoroutinesAndroid = "1.7.3"
    val hiltNavigationComposeVersion = "1.2.0"
    val playServicesVersion = "21.2.0"
    val junitVersion = "4.13.2"
    val extJunitVersion = "1.1.5"
    val espressoVersion = "3.5.1"
    val facebookVersion = "17.0.1"
    val splashScreen = "1.0.0"
    val material = "1.12.0"

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation("com.google.android.material:material:$material")
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleRuntimeVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleRuntimeVersion")

    //FIREBASE API
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:$firebaseVersion"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    //ICONS
    implementation("androidx.compose.material:material-icons-extended:$iconsExtendedVersion")

    //NAVIGATION
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializer")

    //COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesAndroid")

    //Splash Screen
    implementation("androidx.core:core-splashscreen:$splashScreen")

    //DI
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltNavigationComposeVersion")

    //Room
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    //Gson
    implementation("com.google.code.gson:gson:$gsonVersion")

    //Rest services
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //AUTHENTICATION
    // Import the BoM for the Firebase platform
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:$playServicesVersion")
    implementation("androidx.credentials:credentials:$credentials")
    implementation("com.google.android.libraries.identity.googleid:googleid:$googleId")

    //Facebook Api
    implementation("com.facebook.android:facebook-login:$facebookVersion")

    //Load image
    implementation("io.coil-kt:coil-compose:$coilVersion")

    //TESTING
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:$extJunitVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}