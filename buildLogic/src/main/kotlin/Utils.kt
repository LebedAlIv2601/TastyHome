import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.github.frankois944.spmForKmp.utils.ExperimentalSpmForKmpFeature
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.nio.file.FileSystems

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal fun Project.plugin(plugin: Provider<PluginDependency>) {
    plugins.apply(plugin.get().pluginId)
}

internal fun version(version: Provider<String>): Int {
    return version.get().toInt()
}

internal fun Project.createNamespace(): String {
    val correctNameSpace =
        this.projectDir.path.substringAfter(
            this.rootProject.projectDir.name + FileSystems.getDefault().separator
        ).replace(FileSystems.getDefault().separator, ".").let { "$NAMESPACE.$it" }
    return correctNameSpace
}

internal fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

internal fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

internal fun Project.withKmp(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension>(block)
}

fun Project.androidDependencies(block: KotlinDependencyHandler.() -> Unit) {
    withKmp { sourceSets.androidMain.dependencies(block) }
}

class IosKotlinDependencyHandler(private val dependencyHandler: KotlinDependencyHandler) :
    KotlinDependencyHandler by dependencyHandler {

    internal val spmPackages: MutableList<IosSpmDeps> = mutableListOf()

    fun spm(dependency: IosSpmDeps) {
        if(!spmPackages.contains(dependency)) spmPackages.add(dependency)
    }
}

@OptIn(ExperimentalSpmForKmpFeature::class)
fun Project.iosDependencies(block: IosKotlinDependencyHandler.() -> Unit) {
    withKmp {
        var iosDepHandler: IosKotlinDependencyHandler? = null
        sourceSets.iosMain.dependencies {
            block(IosKotlinDependencyHandler(this).also { iosDepHandler = it })
        }

        iosDepHandler?.spmPackages?.takeIf { it.isNotEmpty() }?.let { packages ->
            plugin(libs.plugins.spmForKmp)
            iosTargets.forEach {
                packages.forEach { spmDependency ->
                    it.swiftPackageConfig(cinteropName = spmDependency.cinteropName) {
                        minIos = version(libs.versions.minIos).toString()
                        dependency {
                            val products = spmDependency.products
                            remotePackageVersion(
                                url = uri(spmDependency.url),
                                products = {
                                    products.forEach { product ->
                                        add(product)
                                    }
                                },
                                version = spmDependency.version
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Project.commonDependencies(block: KotlinDependencyHandler.() -> Unit) {
    withKmp { sourceSets.commonMain.dependencies(block) }
}

fun Project.commonTestDependencies(block: KotlinDependencyHandler.() -> Unit) {
    withKmp { sourceSets.commonTest.dependencies(block) }
}

val KotlinMultiplatformExtension.iosTargets: List<KotlinNativeTarget>
    get() = listOf(iosArm64(), iosSimulatorArm64(), iosX64())