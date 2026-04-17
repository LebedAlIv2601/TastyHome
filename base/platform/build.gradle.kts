plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableKotlinSerialization()

androidDependencies {
    implementation(libs.androidx.ktx.core)
    implementation(libs.androidx.activity.ktx)
}

commonDependencies {
    implementation(projects.base.foundation)
    implementation(projects.base.logger)
    implementation(libs.kotlinx.io)
}