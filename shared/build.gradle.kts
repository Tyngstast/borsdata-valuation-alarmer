plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("kotlinx-serialization")
    id("co.touchlab.kermit")
}

group = "com.github.tyngstast"
version = "0.0.1"

kotlin {
    android()
    ios()
    iosSimulatorArm64()

    val coroutinesVersion: String by project
    val kermitVersion: String by project
    val lifecycleVersion: String by project
    val sqlDelightVersion: String by project
    val koinVersion: String by project
    val serializationVersion = "1.3.3"
    val ktorVersion = "2.1.2"
    val kVaultVersion = "1.7.0"
    val settingsVersion = "0.8.1"
    val datetimeVersion = "0.3.3"

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "15.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            isStatic = false // SwiftUI preview requires dynamic framework
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetimeVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")
                implementation("com.liftric:kvault:$kVaultVersion")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("com.russhwolf:multiplatform-settings:$settingsVersion")
                implementation("co.touchlab:kermit:$kermitVersion")
//                implementation("co.touchlab:kermit-crashlytics:$kermitVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}

val releaseBuild: String by project
kermit {
    if (releaseBuild.toBoolean()) {
        stripBelow = co.touchlab.kermit.gradle.StripSeverity.Info
    }
}

sqldelight {
    database("ValueAlarmerDb") {
        packageName = "com.github.tyngstast.db"
        deriveSchemaFromMigrations = true
    }
}
