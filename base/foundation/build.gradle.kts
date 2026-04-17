plugins {
    alias(libs.plugins.convention.baseKmp)
}

commonDependencies {
    api(libs.kotlinx.datetime)
    implementation(projects.base.logger)
}