plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableKotlinSerialization()
enableMetro()

commonDependencies {
    implementation(libs.ktor.core)
    implementation(projects.base.foundation)
    implementation(projects.base.network)
}