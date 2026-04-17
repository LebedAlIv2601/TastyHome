plugins {
    alias(libs.plugins.convention.baseKmp)
}

enableCompose()

androidDependencies {
    implementation(libs.androidx.ktx.core)
}

commonDependencies {
    implementation(projects.base.foundation)
}