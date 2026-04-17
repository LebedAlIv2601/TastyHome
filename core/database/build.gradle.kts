plugins {
    alias(libs.plugins.convention.baseKmp)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

enableMetro()

room {
    schemaDirectory("$projectDir/schemas")
}

commonDependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.sqliteBundled)
    implementation(libs.kotlinx.datetime)

    implementation(projects.base.foundation)
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}