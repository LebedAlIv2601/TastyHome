rootProject.name = "KmpProjectTemplate"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("buildLogic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(
    // apps
    ":shared",
    ":androidApp",

    // base
    ":base:ui",
    ":base:foundation",
    ":base:network",
    ":base:logger",
    ":base:presentation",
    ":base:domain",
    ":base:platform",
    ":base:localStorage",
    ":base:navigation:api",
    ":base:navigation",

    // core
    ":core:database",
    ":core:network:baseClient",
    ":core:language:api",
    ":core:language:impl",
    ":core:designSystem",
    ":core:themeManager:api",
    ":core:themeManager:impl",
)