plugins {
 id("com.android.library")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.compose")
}
android {
 namespace="com.simplesoft.core"
 compileSdk=36
 defaultConfig {minSdk=26}
 compileOptions {sourceCompatibility=JavaVersion.VERSION_17;targetCompatibility=JavaVersion.VERSION_17}
 buildFeatures {compose=true}
}
kotlin {compilerOptions {jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)}}
dependencies {
 implementation(platform("androidx.compose:compose-bom:2025.10.01"))
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
 testImplementation("junit:junit:4.13.2")
 testImplementation("org.json:json:20240303")
}
