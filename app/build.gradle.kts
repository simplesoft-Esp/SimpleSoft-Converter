plugins {
 id("com.android.application")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.compose")
 id("org.jetbrains.kotlin.kapt")
}
android {
 namespace = "com.simplesoft.converter"
 compileSdk = 36
 defaultConfig { applicationId = "com.simplesoft.converter"; minSdk = 26; resourceConfigurations += listOf("en", "es"); targetSdk = 36; versionCode = 6; versionName = "0.3.1"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
 buildTypes { release { isMinifyEnabled = false } }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }

 buildFeatures { compose = true }
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
kapt { correctErrorTypes = true; arguments { arg("room.schemaLocation", "$projectDir/schemas") } }
dependencies {
 androidTestImplementation("androidx.test:runner:1.6.2")
 androidTestImplementation("androidx.test.ext:junit:1.2.1")
 androidTestImplementation("androidx.test:core:1.6.1")
 implementation(project(":simplesoft-core"))
 implementation("androidx.core:core-splashscreen:1.0.1")
 implementation(platform("androidx.compose:compose-bom:2025.10.01"))
 implementation("androidx.core:core-ktx:1.17.0")
 implementation("androidx.activity:activity-compose:1.11.0")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
 implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
 implementation("androidx.datastore:datastore-preferences:1.1.7")
 implementation("androidx.room:room-runtime:2.8.3")
 implementation("androidx.room:room-ktx:2.8.3")
 kapt("androidx.room:room-compiler:2.8.3")
 testImplementation("junit:junit:4.13.2")
 testImplementation("org.json:json:20240303")
 testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}
