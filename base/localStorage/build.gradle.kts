plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableKotlinSerialization()

commonDependencies {
    implementation(projects.base.foundation)
    api(libs.datastore)
}