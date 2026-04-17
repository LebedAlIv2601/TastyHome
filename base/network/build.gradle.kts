plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableKotlinSerialization()

commonDependencies {
    implementation(libs.ktor.core)
    implementation(libs.ktor.json)
    implementation(libs.ktor.negotiation)
    implementation(libs.kotlinx.datetime)

    implementation(projects.base.logger)
    implementation(projects.base.domain)
}

androidDependencies {
    implementation(libs.ktor.android)
}

iosDependencies {
    implementation(libs.ktor.darwin)
}