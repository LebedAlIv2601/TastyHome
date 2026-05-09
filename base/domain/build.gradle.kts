plugins {
    alias(libs.plugins.convention.baseKmp)
}

commonDependencies {
    implementation(projects.base.foundation)
}