plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableDecompose()

commonDependencies {
    implementation(projects.base.domain)
    implementation(projects.base.foundation)
}