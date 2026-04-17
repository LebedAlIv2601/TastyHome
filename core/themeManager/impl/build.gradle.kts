plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableMetro()
enableLocalStorage()

commonDependencies {
    api(projects.core.themeManager.api)
    implementation(projects.base.foundation)
}