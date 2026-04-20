plugins {
    alias(libs.plugins.convention.baseKmp)
}

configureUiFeature()
enableCoil()

commonDependencies {
    api(projects.features.homeRecipes.api)

    implementation(projects.base.navigation)
    implementation(projects.base.domain)
    implementation(projects.base.presentation)
    implementation(projects.base.network)
    implementation(projects.base.foundation)

    implementation(projects.core.designSystem)
    implementation(projects.core.network.baseClient)
}
