import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BaseKmpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugin(libs.plugins.kotlinMultiplatform)
            plugin(libs.plugins.androidLibrary)

            withKmp {
                this.extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                    namespace = createNamespace()
                    compileSdk = version(libs.versions.android.compileSdk)
                    minSdk = version(libs.versions.android.minSdk)
                    androidResources.enable = true
                }
                iosTargets
            }
            commonDependencies {
                implementation(libs.coroutines)
            }
        }
    }
}