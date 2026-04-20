plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableCompose()
enableMetro()
enableKotlinSerialization()

commonDependencies {
    // libs
    implementation(libs.kermit)
    api(libs.decompose.core)
    api(libs.decompose.compose)
    api(libs.decompose.compose.experimental)
    api(libs.decompose.lifecycle)
    api(libs.decompose.lifecycle.coroutines)
    api(libs.decompose.backhandler)
    api(libs.decompose.statekeeper)

    // base
    api(projects.base.logger)
    api(projects.base.platform)
    api(projects.base.network)
    api(projects.base.navigation)
    implementation(projects.base.presentation)
    implementation(projects.base.localStorage)
    implementation(projects.base.foundation)

    // core
    api(projects.core.network.baseClient)
    implementation(projects.core.database)
    implementation(projects.core.language.impl)
    implementation(projects.core.themeManager.impl)
    implementation(projects.core.designSystem)

    // features
    implementation(projects.features.homeRecipes.impl)
}

commonTestDependencies {
    implementation(libs.kotlin.test)
}

kotlin {
    iosTargets.forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            linkerOpts.add("-lsqlite3")

            export(libs.decompose.core)
            export(libs.decompose.compose)
            export(libs.decompose.compose.experimental)
            export(libs.decompose.lifecycle)
            export(libs.decompose.lifecycle.coroutines)
            export(libs.decompose.backhandler)
            export(libs.decompose.statekeeper)
            export(projects.base.logger)
            export(projects.base.platform)
            export(projects.base.foundation)
            export(projects.base.network)
            export(projects.core.network.baseClient)
            export(projects.base.navigation)
        }
    }
}
