plugins {
    alias(libs.plugins.convention.baseKmp)
    alias(libs.plugins.composeCompiler)
}

commonDependencies {
    api(libs.compose.runtime)
    api(libs.compose.foundation)
    api(libs.decompose.core)
    api(projects.base.foundation)
}
