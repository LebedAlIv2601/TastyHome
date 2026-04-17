plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableCompose()

commonDependencies {
    implementation(projects.base.ui)
    implementation(projects.core.themeManager.api)
}