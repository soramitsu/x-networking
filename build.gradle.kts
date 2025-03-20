plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.9.0").apply(false)
    id("com.android.library").version("8.9.0").apply(false)
    id("com.squareup.sqldelight").version("1.5.5").apply(false)
    kotlin("android").version("2.1.10").apply(false)
    kotlin("multiplatform").version("2.1.10").apply(false)
    kotlin("plugin.serialization").version("2.1.10").apply(false)
    id("org.sonarqube") version "5.0.0.4638"
    id("com.apollographql.apollo") version "4.0.0-rc.1"
    id("com.google.devtools.ksp").version("2.1.10-1.0.31").apply(false)
    id("org.jetbrains.kotlin.plugin.compose").version("2.1.10").apply(false)
    id("org.jetbrains.kotlinx.kover").version("0.8.3").apply(false)
}
val sourceCompatibility by extra(JavaVersion.VERSION_1_8)

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    afterEvaluate {
        tasks.register("testClasses") {}
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

sonarqube {
    properties {
        property("sonar.projectKey", "sora:x-networking")
        property("sonar.projectName", "x-networking")
        property("sonar.exclusions", "**/*.txt,**/*.kts")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.rootDir}/report/coverage.xml")
    }
}
