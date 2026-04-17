plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableCompose()
enableDecompose()

commonDependencies {
    api(projects.base.navigation.api)
    api(projects.base.foundation)
}