import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
    }
}

dependencies {
    // все работает, не обращать внимание на подсвечиваемую ошибку
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    compileOnly(libs.gradlePlugin.android)
    compileOnly(libs.gradlePlugin.kotlin)
    compileOnly(libs.gradlePlugin.spmForKmp)
}

gradlePlugin {
    plugins {
        register("android-app-plugin") {
            id = "com.something.android-app-plugin"
            implementationClass = "AndroidAppPlugin"
        }
        register("base-kmp-plugin") {
            id = "com.something.base-kmp-plugin"
            implementationClass = "BaseKmpPlugin"
        }
    }
}