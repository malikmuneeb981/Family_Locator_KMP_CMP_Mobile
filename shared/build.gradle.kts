import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    android {
       namespace = "org.example.project.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.documentfile)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.accompanist.permissions)
            implementation(libs.koin.android)
            implementation ("com.google.android.gms:play-services-gcm:16.1.0")
            implementation ("com.google.android.gms:play-services-maps:18.1.0")
            implementation ("com.google.maps.android:maps-compose:2.11.3")
            implementation(libs.play.services.location)
            implementation("com.google.zxing:core:3.5.4")
            implementation("androidx.camera:camera-core:1.6.2")
            implementation("androidx.camera:camera-camera2:1.6.2")
            implementation("androidx.camera:camera-lifecycle:1.6.2")
            implementation("androidx.camera:camera-view:1.6.2")
            implementation("com.google.mlkit:barcode-scanning:17.3.0")

        }
        commonMain {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.navigation.compose)
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor3)
                implementation(libs.coil.video)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.coil.gif)
                implementation(libs.material.icons.extended)
                implementation(libs.compose.multiplatform.media.player)
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
            }
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
