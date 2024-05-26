plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebase.crashlytics)
    id("kotlin-kapt")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.education.ekagratagkquiz"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.education.ekagratagkquiz"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 19
        versionName = "1.9.0"


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        // We use a bundled debug keystore, to allow debug builds from CI to be upgradable
        named("debug") {
            storeFile = rootProject.file("keystore.jks")
            storePassword = "Rakesh@2000"
            keyAlias = "upload"
            keyPassword = "Rakesh@2000"
        }
    }


    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }


    buildTypes {

        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
//            isMinifyEnabled = true
//            isShrinkResources = true

            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    /*splits {
        // Configures multiple APKs based on ABI.
        abi {

            // Enables building multiple APKs per ABI.
            isEnable = true

            // By default all ABIs are included, so use reset() and include to specify that you only
            // want APKs for x86 and x86_64.

            // Resets the list of ABIs for Gradle to create APKs for to none.
            reset()

            // Specifies a list of ABIs for Gradle to create APKs for.
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")

            // Specifies that you don't want to also generate a universal APK that includes all ABIs.
            isUniversalApk = false
        }

        // Configures multiple APKs based on screen density.
        density {

            // Configures multiple APKs based on screen density.
            isEnable = true

            // Specifies a list of screen densities you don't want Gradle to create multiple APKs for.
            exclude("ldpi", "xxhdpi", "xxxhdpi")

            // Specifies a list of compatible screen size settings for the manifest.
            compatibleScreens("small", "normal", "large", "xlarge")
        }
    }*/


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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation)
    implementation(libs.io.coil.kt)
    implementation(libs.icons.extended)

    implementation(libs.androidx.datastore)

    implementation(libs.dagger.hilt.navigation)
    implementation(libs.dagger.hilt.compose)
    implementation(libs.dagger.hilt)


    implementation(libs.androidx.window)
    implementation(libs.androidx.compose.materialWindow)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.exifinterface)

    implementation(libs.accompanist.swiperefresh)
    implementation(libs.firebase.messaging.ktx)

    kapt(libs.dagger.hilt.compiler)


    implementation(libs.play.services.ads.identifier)
    implementation(libs.google.guava)


    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)



    implementation(libs.apache.excel.ooxml)
    implementation(libs.apache.excel)
    implementation(libs.pdf.reader.bouquet)
    implementation(libs.pdf.reader.pratikksahu)

    implementation(libs.google.ads)
    implementation(libs.google.billingClient)
    implementation(libs.core.splashscreen)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}