@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.shadow)
}

kotlin {
    jvm {
        mainRun {
            mainClass = "org.malv.youtube.MainKt"
        }
        tasks.register<ShadowJar>("jvmShadowJar") {
            val mainCompilation = compilations["main"]
            val jvmRuntimeConfiguration = mainCompilation
                .runtimeDependencyConfigurationName
                .let { project.configurations[it] }

            from(mainCompilation.output.allOutputs)
            configurations = listOf(jvmRuntimeConfiguration)
            archiveClassifier.set("fatjar")
            manifest.attributes("Main-Class" to "org.malv.youtube.MainKt")
            archiveVersion.set("0.8")
            archiveFileName.set("youtube-codes-${archiveVersion.get()}.jar")
            destinationDirectory.set(file("${rootProject.projectDir}/release"))
        }

    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.google.api)
            implementation(libs.google.auth)
            implementation(libs.google.youtube)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }

    jvmToolchain(21)

}
compose {
    composeHotReload {

    }
}
compose.desktop {
    application {
        mainClass = "org.malv.youtube.MainKt"

        buildTypes {
            release {
                proguard {
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }
            }
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "youtube-codes"
            packageVersion = "1.0.0"
        }
    }
}
