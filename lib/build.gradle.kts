import org.apache.groovy.json.internal.Chr.add
import org.jetbrains.kotlin.config.JvmTarget

plugins {
    id("maven-publish")

    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")

    kotlin("plugin.serialization")

    id("com.squareup.sqldelight")
    id("com.apollographql.apollo")

    id("com.google.devtools.ksp")

    id("org.jetbrains.kotlinx.kover")

    id("co.touchlab.kmmbridge") version "0.5.5"
    id("io.mockative") version "3.0.1"
}

val libVersion: String by project
val iosLibName = "XNetworking"

group = "jp.co.soramitsu.xnetworking"
version = libVersion

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu.xnetworking"
            artifactId = "lib"
            version = libVersion

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "scnRepo"
            url =
                uri(if (hasProperty("RELEASE_REPOSITORY_URL")) property("RELEASE_REPOSITORY_URL")!! else System.getenv()["RELEASE_REPOSITORY_URL"]!!)
            credentials {
                username =
                    if (hasProperty("NEXUS_USERNAME")) (property("NEXUS_USERNAME") as String) else System.getenv()["NEXUS_USERNAME"]
                password =
                    if (hasProperty("NEXUS_PASSWORD")) (property("NEXUS_PASSWORD") as String) else System.getenv()["NEXUS_PASSWORD"]
            }
        }
    }
}

/**
 * Use ./gradlew spmDevBuild to publish iOS binaries locally,
 * and generate corresponding Package.swift file
 *
 * Use ./gradlew kmmBridgePublish to publish iOS binaries as zip file remotely,
 * and generate corresponding Package.swift file
 */
kmmbridge {
    frameworkName = iosLibName
    manualVersions()
    mavenPublishArtifacts(
        repository = "scnRepo",
        publication = "releaseIOS",
        artifactSuffix = "IOS" // this is exactly Suffix, not artifact's full name
    )
    spm(
        /* spmDirectory = "Location of Package.Swift", if not specified, Package.Swift fill will located at the project's root, that's most convinient for iOS team */
        /* useCustomPackageFile = false, if specified true, Package.swift file for IOS can become corrupt */
        swiftToolVersion = "5.6" // contact with iOS team before changing this value
    ) { iOS { v("14") } }
}

val coroutineVersion: String by project
val apolloGraphQLVersion: String by project
val ktorVersion: String by project
val sqlDelightVersion: String by project

kotlin {
    /*
        Don't init XCFramework!!!
        Under the hood, each "XCFramework" invocation creates a task

        KMMBridge plugin creates this task on its own, additional invocation will result in error
    */
    // val framework = XCFramework()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishAllLibraryVariants()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = iosLibName
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                api("com.apollographql.apollo:apollo-runtime:$apolloGraphQLVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                //implementation("com.ionspin.kotlin:bignum:0.3.6")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                //implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-serialization:1.0.0")
            }
        }
        val commonTest = getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
                implementation("io.mockative:mockative:3.0.1")
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val androidUnitTest by getting

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
    androidTarget() {
        publishAllLibraryVariants()
    }
}

dependencies {
    /* KSP is used for test mock generation */
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:3.0.1")
        }
}

sqldelight {
    database("SoraHistoryDatabase") {
        packageName = "jp.co.soramitsu.xnetworking.db"
        schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    namespace = "jp.co.soramitsu.xnetworking.lib"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

apollo {
    service("westend") {
        packageName.set("jp.co.soramitsu.xnetworking.westend")
        schemaFiles.setFrom(file("../schema/westend_schema.graphqls"))
        srcDir(file("${project.projectDir}/src/commonMain/qraphql/queries/txhistory/westend"))
        outputDir.set(File("${project.buildDir}/generated/apollo/westend", "schemas"))
        generateDataBuilders.set(true)

        mapScalarToKotlinString("Cursor")
        mapScalarToKotlinString("BigFloat")

        mapScalar(
            "JSON",
            "kotlinx.serialization.json.JsonElement",
            "jp.co.soramitsu.xnetworking.lib.engines.apollo.impl.adapters.JSONAdapter()"
        )
    }

    service("mainnet") {
        packageName.set("jp.co.soramitsu.xnetworking.mainnet")
        schemaFiles.setFrom(file("../schema/mainnet_schema.graphqls"))
        srcDir(
            files(
                "${project.projectDir}/src/commonMain/qraphql/queries/blockexplorer",
                "${project.projectDir}/src/commonMain/qraphql/queries/txhistory/mainnet"
            )
        )
        outputDir.set(File("${project.buildDir}/generated/apollo/mainnet", "schemas"))
        generateDataBuilders.set(true)

        mapScalarToKotlinString("Cursor")
        mapScalarToKotlinString("BigFloat")

        mapScalar(
            "JSON",
            "kotlinx.serialization.json.JsonElement",
            "jp.co.soramitsu.xnetworking.lib.engines.apollo.impl.adapters.JSONAdapter()"
        )
    }
}

tasks.register<Copy>("copyiOSTestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosX64/debugTest/resources")
}

kover {
    useJacoco()
    reports {
        variant("release") {
            filters {
                excludes {
                    classes(
                        "*.BuildConfig",
                        "**.models.*",
                        "**.core.network.*",
                        "**.di.*",
                        "**.shared_utils.wsrpc.*",
                        "*NetworkDataSource",
                        "*NetworkDataSource\$*",
                        "*ChainConnection",
                        "*ChainConnection\$*",
                        "**.runtime.definitions.TypeDefinitionsTreeV2",
                        "**.runtime.definitions.TypeDefinitionsTreeV2\$*",

                        // TODO: Coverage these modules by tests
                        "**.core.rpc.*",
                        "**.core.utils.*",
                        "**.core.extrinsic.*",
                    )
                }
            }

            xml {
                onCheck = true
                xmlFile = file("${project.rootDir}/report/coverage.xml")
            }

            html {
                onCheck = true
            }

            verify {
                onCheck = true

                rule {
                    disabled = false

                    minBound(5)
                }
            }
        }
    }
}
