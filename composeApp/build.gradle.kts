import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.cocoapods)

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-Xbinary=bundleId=org.example.ComposeApp")
            export("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("coroutinesVersion")}")
            export("com.google.mlkit:barcode-scanning:${findProperty("MLKitBarcodeScanningVersion")}")
            export("com.google.mlkit:common:${findProperty("MLKitCommonVersion")}")
            export("com.google.mlkit:vision:${findProperty("MLKitVisionVersion")}")
        }
    }

    cocoapods {
        summary = "Aplicación de escaneo de códigos de barras"
        homepage = "https://developers.google.com/ml-kit/release-notes?hl=es-419"
        version = "1.0"
        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("MLKitBarcodeScanning", findProperty("MLKitBarcodeScanningVersion") as String)
        pod("MLKitCommon", findProperty("MLKitCommonVersion") as String)
        pod("MLKitVision", findProperty("MLKitVisionVersion") as String)
    }


    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.play.services.mlkit.barcode.scanning)
            implementation(libs.camera.core)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
            implementation(libs.camera.camera2)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("coroutinesVersion")}")
        }
        iosMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${findProperty("coroutinesVersion")}")
        }

    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

