import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

// apply gms & firebase plugin only for gms build flavour
if (gradle.startParameter.taskRequests.toString().contains("gms", ignoreCase = true)) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
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
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.adaptive)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.json)
            implementation(libs.androidx.navigation)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.annotations)

            implementation(libs.settings.noArg)
            implementation(libs.settings.coroutines)
            implementation(libs.settings.serialization)

            implementation(libs.ktor.client.core)
            implementation(libs.okio)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)

            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.okio)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.cio)
            implementation(libs.mp3spi)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.okio.fakefilesystem)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    // KSP Common sourceSet
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

    sourceSets {
        // ...
        getByName("iosX64Main") {
            kotlin.srcDir("build/generated/ksp/iosX64/iosX64Main/kotlin")
        }
        getByName("iosArm64Main") {
            kotlin.srcDir("build/generated/ksp/iosArm64/iosArm64Main/kotlin")
        }
        getByName("iosSimulatorArm64Main") {
            kotlin.srcDir("build/generated/ksp/iosSimulatorArm64/iosSimulatorArm64Main/kotlin")
        }
    }
}

val projectPackageName = libs.versions.project.packageName.get()
// load app signing configurations from local.properties file
val localProperties = getLocalProperties(rootProject)

android {
    namespace = projectPackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = projectPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.project.versionCode.get().toInt()
        versionName = libs.versions.project.versionName.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {

        if (localProperties.hasDebugStoreConfig()) {
            getByName("debug") {
                keyAlias = localProperties["DEBUG_KEY_ALIAS"] as? String
                keyPassword = localProperties["DEBUG_KEY_PASSWORD"] as? String
                storeFile = file(localProperties["DEBUG_STORE_FILE"] as String)
                storePassword = localProperties["DEBUG_STORE_PASSWORD"] as? String
            }
        }
        if (localProperties.hasReleaseStoreConfig()) {
            create("release") {
                keyAlias = localProperties["RELEASE_KEY_ALIAS"] as? String
                keyPassword = localProperties["RELEASE_KEY_PASSWORD"] as? String
                storeFile = file(localProperties["RELEASE_STORE_FILE"] as String)
                storePassword = localProperties["RELEASE_STORE_PASSWORD"] as? String
            }
        }
    }


    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = when {
                localProperties.hasReleaseStoreConfig() -> signingConfigs.getByName("release")
                else -> signingConfigs.getByName("debug")
            }
        }
    }

    flavorDimensions += "services"
    productFlavors {
        //a build flavor with google analytics & crashlytics dependencies
        create("gms") {
            isDefault = true
            dimension = "services"
        }
        //a build flavor free of analytics dependencies
        create("default") {
            dimension = "services"
            dependenciesInfo {
                // Disables dependency metadata when building APKs.
                includeInApk = false
                // Disables dependency metadata when building Android App Bundles.
                includeInBundle = false
            }
        }
    }

    dependencies {
        "gmsImplementation"(platform(libs.firebase.bom))
        "gmsImplementation"(libs.firebase.crashlytics.ktx)
        "gmsImplementation"(libs.firebase.analytics.ktx)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.desktop {
    application {
        mainClass = "$projectPackageName.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Dmg, TargetFormat.Deb)
            packageName = "Quran App"
            packageVersion = libs.versions.project.versionName.get()
            vendor = libs.versions.project.vendor.get()
            val commonIcon = "src/commonMain/composeResources/drawable/icon.ico"
            windows {
                iconFile.set(project.file(commonIcon))
                shortcut = true
            }
            linux {
                iconFile.set(project.file(commonIcon))
                shortcut = true
            }
            macOS { iconFile.set(project.file(commonIcon)) }

            buildTypes.release.proguard {
                isEnabled.set(false)
                obfuscate.set(false)
            }

            tasks.withType<JavaExec>().configureEach {
                if (name.contains("release", ignoreCase = true)) {
                    systemProperty("app.build.mode", "release")
                } else {
                    systemProperty("app.build.mode", "debug")
                }
            }
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// KSP Tasks
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler) // Example for iosX64
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

// Trigger Common Metadata Generation from Native tasks
tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }
    .configureEach {
        dependsOn("kspCommonMainKotlinMetadata")
    }

/**
 * Loads properties from the `local.properties` file in the project root.
 *
 * @param root The project root.
 * @return Properties loaded from `local.properties`
 */
fun getLocalProperties(root: Project): Properties {
    val localProperties = Properties()
    val localPropertiesFile = root.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    return localProperties
}

fun Properties.hasDebugStoreConfig() = this.containsKey("DEBUG_STORE_FILE")
fun Properties.hasReleaseStoreConfig() = this.containsKey("RELEASE_STORE_FILE")