import com.android.build.api.variant.ApplicationVariant
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application") // Kotlin support is built into AGP 9; no kotlin("android")
    kotlin("plugin.serialization") version "2.3.20"
    kotlin("plugin.compose") version "2.3.20"
}

android {
    compileSdk = 37

    defaultConfig {
        applicationId = "com.paoneking.nepallipikeyboard"
        minSdk = 23
        targetSdk = 36
        versionCode = 3
        versionName = "1.1.1"
        // Play splits the bundle per ABI, so shipping x86 there costs arm users
        // nothing -- but it serves no one either: there are no x86 Android phones,
        // and ChromeOS runs arm binaries through translation. Kept out of the
        // bundle, and available in a universal APK for emulators and sideloading:
        //   ./gradlew assembleRelease -PuniversalApk=true
        ndk {
            abiFilters.clear()
            abiFilters.addAll(
                if (providers.gradleProperty("universalApk").orNull == "true")
                    listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
                else listOf("armeabi-v7a", "arm64-v8a")
            )
        }
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    // Release signing. The keystore and its passwords are deliberately not in the
    // repository: keystore.properties is gitignored, and CI can supply the same four
    // values as environment variables instead. If neither is present the release
    // build still assembles -- unsigned, exactly as before -- so a fresh clone is
    // not broken by a missing key.
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties().apply {
        if (keystorePropertiesFile.exists()) keystorePropertiesFile.inputStream().use { load(it) }
    }
    fun signingValue(key: String, env: String): String? =
        keystoreProperties.getProperty(key) ?: System.getenv(env)

    val releaseStoreFile = signingValue("storeFile", "ANDROID_KEYSTORE_FILE")
    val hasReleaseSigning = releaseStoreFile != null && rootProject.file(releaseStoreFile).exists()

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile!!)
                storePassword = signingValue("storePassword", "ANDROID_KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "ANDROID_KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            // res/raw/keep.xml protects the resources fetched via getIdentifier
            isShrinkResources = true
            isDebuggable = false
            isJniDebuggable = false
        }
        create("nouserlib") { // same as release, but does not allow the user to provide a library
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            isJniDebuggable = false
        }
        debug {
            // "normal" debug has minify for smaller APK to fit the GitHub 25 MB limit when zipped
            // and for better performance in case users want to install a debug APK
            isMinifyEnabled = true
            isJniDebuggable = false
            applicationIdSuffix = ".debug"
        }
        create("runTests") { // build variant for running tests on CI that skips tests known to fail
            isMinifyEnabled = false
            isJniDebuggable = false
        }
        create("debugNoMinify") { // for faster builds in IDE
            isDebuggable = true
            isMinifyEnabled = false
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
        }

        androidComponents.onVariants { variant: ApplicationVariant ->
            if (variant.buildType == "debug") {
                // got a little too big for GitHub after some dependency upgrades, so we remove the largest dictionary
                variant.androidResources.ignoreAssetsPatterns = listOf("main_ro.dict")
                variant.proguardFiles = emptyList()
                //noinspection ProguardAndroidTxtUsage we intentionally use the "normal" file here
                variant.proguardFiles.add(project.layout.buildDirectory.file(project.buildFile.parent + "/dontoptimize.pro"))
                variant.proguardFiles.add(project.layout.buildDirectory.file(project.buildFile.parent + "/proguard-rules.pro"))
            }
            variant.outputs.forEach { output ->
                output.outputFileName.set("NepalLipiKeyboard_${defaultConfig.versionName}-${variant.buildType}.apk")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    /*externalNativeBuild {
        ndkBuild {
            path = File("src/main/jni/Android.mk")
        }
    }*/
//    ndkVersion = "28.0.13004108"
//    ndkVersion = "22.1.7171670"
    packaging {
        jniLibs {
            // Must stay false for 16 KB page-size devices (Android 15+): legacy packaging
            // compresses the .so files, and the loader then cannot map them directly from
            // the APK on a 16 KB boundary. Costs ~3 MB of installed size; the download is
            // unchanged because the APK is zipped either way.
            useLegacyPackaging = false
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // see https://github.com/HeliBorg/HeliBoard/issues/477
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    namespace = "com.paoneking.nepallipikeyboard.latin"
    lint {
        abortOnError = true
    }
}

dependencies {
    // androidx
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.autofill:autofill:1.3.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // compose
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    "debugNoMinifyImplementation"("androidx.compose.ui:ui-tooling")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("sh.calvin.reorderable:reorderable:3.1.0") // for easier re-ordering
    implementation("com.github.skydoves:colorpicker-compose:1.1.3") // for user-defined colors

    implementation("com.jakewharton.timber:timber:5.0.1")

    // test
    // AGP 9's built-in Kotlin does not apply KGP, so the bare kotlin("test") artifact no
    // longer resolves to the JVM/JUnit variant. Name it explicitly.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.3.20")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("androidx.test:runner:1.7.0")
    testImplementation("androidx.test:core:1.7.0")
}
