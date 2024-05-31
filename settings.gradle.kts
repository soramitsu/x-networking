pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "xnetworking"
include(":androidApp")
include(":lib:basic")
include(":lib:fearlesswallet")
include(":lib:sorawallet")