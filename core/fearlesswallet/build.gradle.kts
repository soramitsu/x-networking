import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("com.apollographql.apollo3") version "3.8.2"
}

group = "jp.co.soramitsu.xnetworking"

version = "0.1.1"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu.xnetworking"
            artifactId = "fearlesswallet"
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
    val iosFrameworkName = "fearlesswallet"
    val xcf = XCFramework()

    android()
    iosX64 {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
        compilations.forEach {
            it.kotlinOptions.freeCompilerArgs += arrayOf("-linker-options", "-lsqlite3")
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
        compilations.forEach {
            it.kotlinOptions.freeCompilerArgs += arrayOf("-linker-options", "-lsqlite3")
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
        compilations.forEach {
            it.kotlinOptions.freeCompilerArgs += arrayOf("-linker-options", "-lsqlite3")
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Public
                api(project(":core:basic"))

                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":core:basic"))

                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
            }
        }

        val androidMain by getting

        val androidAndroidTest by getting

        val androidTest by getting {
            dependsOn(commonTest)
            androidAndroidTest.dependsOn(this)
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
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

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    namespace = "jp.co.soramitsu.xnetworking.fearlesswallet"
}

apollo {
    service("fearlesswallet") {
        packageName.set("jp.co.soramitsu.xnetworking.fearlesswallet")
        schemaFiles.setFrom(file("../../schema/fearless_westend_schema.graphqls"))
        srcDir(file("${project.projectDir}/src/commonMain/graphql"))
        outputDir.set(File("${project.buildDir}/generated/apollo/", "schemas"))

        mapScalarToKotlinString("Cursor")
        mapScalarToKotlinString("BigFloat")

        mapScalar(
            "JSON",
            "kotlinx.serialization.json.JsonElement",
            "jp.co.soramitsu.xnetworking.basic.engines.apollo.impl.adapters.JSONAdapter()"
        )
    }
}

tasks.register<Copy>("copyiOSTestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosX64/debugTest/resources")
}

tasks.findByName("iosX64Test")!!.dependsOn("copyiOSTestResources")
