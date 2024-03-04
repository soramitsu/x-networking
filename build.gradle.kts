plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.3.0").apply(false)
    id("com.android.library").version("8.3.0").apply(false)
    id("com.squareup.sqldelight").version("1.5.5").apply(false)
    kotlin("android").version("1.9.22").apply(false)
    kotlin("multiplatform").version("1.9.22").apply(false)
    kotlin("plugin.serialization").version("1.9.22").apply(false)
    id("org.sonarqube") version "4.4.1.3373"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

sonarqube {
    properties {
        property("sonar.projectKey", "sora:x-networking")
        property("sonar.projectName", "x-networking")
        property("sonar.exclusions", "${project.projectDir}/**/*.txt")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/build/reports/coverage/*.xml")
    }
}
