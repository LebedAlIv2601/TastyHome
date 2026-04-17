plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableMetro()
enableLocalStorage()

commonDependencies {
    api(projects.core.language.api)
    implementation(projects.base.logger)
    implementation(projects.base.foundation)
}

androidDependencies {
    implementation(libs.phoenix)
}