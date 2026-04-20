import org.gradle.api.Project

fun Project.enableCompose() {
    plugin(libs.plugins.composeCompiler)
    plugin(libs.plugins.composeMultiplatform)
    commonDependencies {
        implementation(libs.compose.ui)
        implementation(libs.compose.preview)
        implementation(libs.compose.foundation)
        implementation(libs.compose.runtime)
        implementation(libs.compose.material3)
        implementation(libs.compose.resources)
    }
}

fun Project.enableDecompose() {
    commonDependencies {
        implementation(libs.decompose.core)
        implementation(libs.decompose.compose)
        implementation(libs.decompose.compose.experimental)
        implementation(libs.decompose.lifecycle)
        implementation(libs.decompose.lifecycle.coroutines)
        implementation(libs.decompose.statekeeper)
        implementation(libs.decompose.backhandler)
    }
}

fun Project.enableMetro() {
    plugin(libs.plugins.metro)
}

fun Project.enableCoil() {
    commonDependencies {
        implementation(libs.coil.compose)
        implementation(libs.coil.ktor)
    }

    androidDependencies {
        implementation(libs.ktor.android)
    }

    iosDependencies {
        implementation(libs.ktor.darwin)
    }
}

fun Project.enableKotlinSerialization() {
    plugin(libs.plugins.kotlinSerialization)
    commonDependencies {
        implementation(libs.kotlinSerialization)
    }
}

fun Project.enableDatabase() {
    commonDependencies {
        implementation(project(":core:database"))
    }
}

fun Project.enableLocalStorage() {
    commonDependencies {
        implementation(project(":base:localStorage"))
    }
}

fun Project.configureFeatureApi() {
    commonDependencies {
        implementation(project(":base:navigation:api"))
        implementation(libs.decompose.core)
    }
}

fun Project.configureUiFeature() {
    enableCompose()
    enableDecompose()
    enableKotlinSerialization()
    enableMetro()
    commonDependencies {
        implementation(libs.lifecycle.runtimeCompose)
    }
}