plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.aplicacionfinal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aplicacionfinal"
        minSdk = 34


        this.targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding{
        enable = true
    }
}

dependencies {


    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation ("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //noinspection GradleDependency
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    //noinspection GradleDependency
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))


    implementation("com.google.firebase:firebase-firestore")
    //Analytics
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")

    implementation ("androidx.credentials:credentials:1.5.0-rc01")
    implementation ("androidx.credentials:credentials-play-services-auth:1.5.0-rc01")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.firebase:firebase-analytics")

    //tabs
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.google.android.material:material:1.12.0")



}