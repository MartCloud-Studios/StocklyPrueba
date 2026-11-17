plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val adbName =
    if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "adb.exe" else "adb"
val adbExecutable = androidComponents.sdkComponents.sdkDirectory.map { sdkDirectory ->
    sdkDirectory.file("platform-tools/$adbName").asFile
}

android {
    namespace = "co.edu.univalle.stocklyapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "co.edu.univalle.stocklyapp"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

val reverseBackendPort = tasks.register<Exec>("reverseBackendPort") {
    group = "installation"
    description = "Forwards the local backend port to the Android emulator."
    executable(adbExecutable.get().absolutePath)
    args("reverse", "tcp:8080", "tcp:8080")
}

tasks.matching { it.name == "installDebug" }.configureEach {
    finalizedBy(reverseBackendPort)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navegación entre pantallas
    implementation(libs.androidx.navigation.compose)

    // Iconos en Compose
    implementation(libs.androidx.compose.material.icons.extended)

    // ViewModel + lifecycle (MVVM, evita perder datos al rotar)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Retrofit + Moshi (KSP genera los adaptadores)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Imágenes
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}