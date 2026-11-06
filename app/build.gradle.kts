import java.io.FileInputStream
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
}

val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties().apply {
  if (keystorePropertiesFile.exists()) {
    load(FileInputStream(keystorePropertiesFile))
  }
}

val releaseStoreFilePath = System.getenv("KEYSTORE_FILE")
  ?: keystoreProperties.getProperty("KEYSTORE_FILE")
  ?: "keystore/release.jks"

val releaseStorePassword = System.getenv("KEYSTORE_PASSWORD")
  ?: keystoreProperties.getProperty("KEYSTORE_PASSWORD")
  ?: "musicstream123"

val releaseKeyAlias = System.getenv("KEYSTORE_ALIAS")
  ?: keystoreProperties.getProperty("KEYSTORE_ALIAS")
  ?: "musicstream"

val releaseKeyPassword = System.getenv("KEYSTORE_KEY_PASSWORD")
  ?: keystoreProperties.getProperty("KEYSTORE_KEY_PASSWORD")
  ?: "musicstream123"

android {
  namespace = "com.example.p5"
  compileSdk = 36
  defaultConfig {
    applicationId = "com.example.p5app"
    minSdk = 24
    targetSdk = 36
    versionCode = 7
    versionName = "2.1.0"
  }

  signingConfigs {
    create("release") {
      storeFile = file(releaseStoreFilePath)
      storePassword = releaseStorePassword
      keyAlias = releaseKeyAlias
      keyPassword = releaseKeyPassword
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    debug {
      signingConfig = signingConfigs.getByName("release")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    aidl = false
    buildConfig = true
    shaders = false
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)

  debugImplementation(libs.androidx.compose.ui.tooling)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.session)
  implementation(libs.androidx.media3.ui)
  implementation(libs.androidx.media3.common)

  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.coil.compose)

  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging)
}
