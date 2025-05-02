plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.chatterbox.easychat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chatterbox.easychat"
        minSdk = 26
        targetSdk = 35
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
}

dependencies {
    // Firebase BoM to automatically manage versions
    implementation(platform(libs.firebase.bom.v3270))

    // Firebase modules (these will now automatically use compatible versions from the BoM)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.firebase.auth)
    implementation (libs.firebase.storage.v2000)
    implementation (libs.firebase.auth.v2100)

    // Your existing libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.ccp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.integrity)
    implementation (libs.glide.v4151)  // Make sure to use the latest version
    annotationProcessor (libs.compiler)

}
