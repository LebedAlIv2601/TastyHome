import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidAppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugin(libs.plugins.androidApplication)

            extensions.configure<ApplicationExtension> {
                namespace = APP_PACKAGE
                compileSdk = version(libs.versions.android.compileSdk)

                defaultConfig.apply {
                    applicationId = APP_PACKAGE
                    versionCode = version(libs.versions.android.appVersionCode)
                    versionName = getAppVersionName(this@with)
                    minSdk = version(libs.versions.android.minSdk)
                    targetSdk = version(libs.versions.android.targetSdk)
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
                buildFeatures {
                    resValues = true
                    buildConfig = true
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(version(libs.versions.javaVersion))
                    targetCompatibility = JavaVersion.toVersion(version(libs.versions.javaVersion))
                }
            }
            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
                }
            }
        }
    }

    private fun getAppVersionName(project: Project): String {
        return with(project) {
            val major = version(libs.versions.majorVersion)
            val minor = version(libs.versions.minorVersion)
            val hotfix = version(libs.versions.hotfixVersion)
            "$major.$minor.$hotfix"
        }
    }
}