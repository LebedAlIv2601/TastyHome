import io.gitlab.arturbosch.detekt.Detekt

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.metro) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.spmForKmp) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

val projectSource = file(projectDir)
val configFile = file("$projectDir/codeQuality/detekt/detekt.yml")
val resourceFiles = "**/resources/**"
val kotlinFiles = "**/*.kt"
val testFiles = "**/src/test/**"

tasks.register<Detekt>("detektAll") {
    description = "Custom DETEKT build for all modules"
    ignoreFailures = false
    buildUponDefaultConfig = true
    setSource(projectSource)
    allRules = false
    config.setFrom(configFile)
    include(kotlinFiles)
    exclude(resourceFiles, testFiles)
    reports { html.required.set(true) }
}

tasks.register<Copy>("installGitHook") {
    description = "Copies the pre-push git hook from /scripts/hooks/prepush to the .git folder"
    group = "git hooks"
    from(file("$rootDir/scripts/hooks/prepush/pre-push"))
    into(file("$rootDir/.git/hooks/"))
    filePermissions { unix("755") }
    doLast { println("Git hook installed successfully!") }
}

afterEvaluate {
    tasks.getByPath(":androidApp:preBuild").dependsOn(":installGitHook")
}