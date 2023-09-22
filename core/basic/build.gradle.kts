import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("com.squareup.sqldelight")
}

group = "jp.co.soramitsu.xnetworking"

version = "0.1.1"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu.xnetworking"
            artifactId = "basic"
            version = "0.1.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "scnRepo"
            url = uri(if (hasProperty("RELEASE_REPOSITORY_URL")) property("RELEASE_REPOSITORY_URL")!! else System.getenv()["RELEASE_REPOSITORY_URL"]!!)
            credentials {
                username = if (hasProperty("NEXUS_USERNAME")) (property("NEXUS_USERNAME") as String) else System.getenv()["NEXUS_USERNAME"]
                password = if (hasProperty("NEXUS_PASSWORD")) (property("NEXUS_PASSWORD") as String) else System.getenv()["NEXUS_PASSWORD"]
            }
        }
        maven {
            name = "scnRepoLocal"
            url = uri("${project.buildDir}/scnrepo")
        }
    }
}

val coroutineVersion = "1.6.4"
val ktorVersion = "2.3.1"

kotlin {
    val iosFrameworkName = "basic"
    val xcf = XCFramework()

    android()
    iosX64 {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = iosFrameworkName
        }
    }

    val sqlDelightVersion: String by project
    
    sourceSets {

        val commonMain by getting {
            dependencies {
                // Public
                api("com.apollographql.apollo3:apollo-runtime:3.8.2")
                api("io.ktor:ktor-client-core:$ktorVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                //implementation("io.ktor:ktor-client-json:$ktorVersion")
                api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-client-logging:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

                // Private
                //implementation("com.ionspin.kotlin:bignum:0.3.6")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")

                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-serialization:1.0.0")

                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val androidTest by getting

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
    android {
        publishAllLibraryVariants()
    }
}

sqldelight {
    database("SoraHistoryDatabase") {
        packageName = "jp.co.soramitsu.xnetworking.db"
        schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    namespace = "jp.co.soramitsu.xnetworking.basic"
}

tasks.register<Copy>("copyiOSTestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosX64/debugTest/resources")
}

tasks.findByName("iosX64Test")!!.dependsOn("copyiOSTestResources")
