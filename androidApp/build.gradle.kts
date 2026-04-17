plugins {
    alias(libs.plugins.convention.androidApp)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}


dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.preview)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.tooling)
    implementation(libs.decompose.core)
    implementation(libs.decompose.compose)

    implementation(projects.shared)
}