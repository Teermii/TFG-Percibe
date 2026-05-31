import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

// Leemos la MAPS_API_KEY desde local.properties (que esta en .gitignore)
// Asi la API key NUNCA acaba en el repositorio. Si no existe, usamos string vacia
// y la app se compila igual (Maps simplemente no funcionara hasta poner la key).
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val mapsApiKey: String = (localProperties.getProperty("MAPS_API_KEY")
    ?: project.findProperty("MAPS_API_KEY")?.toString()
    ?: "")

android {
    namespace = "com.example.alarmaapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.alarmaapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // GoogleMaps
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Segundo plano
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Componenetes modernos android
    implementation("com.google.android.material:material:1.11.0")
}