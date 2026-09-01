plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
}

dependencies {
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    // dependencies
    implementation(platform(libs.spring.boot.dependencies.get().toString()))
    implementation(platform(libs.kotlin.bom.get().toString()))
    implementation(libs.annotations.get().toString())
    implementation(libs.commons.io.get().toString())
    implementation(libs.commons.codec.get().toString())
    implementation(libs.commons.compress.get().toString())
    implementation(libs.commons.lang.get().toString())
    implementation(libs.commons.pool.get().toString())
    // plugins
    implementation(libs.spring.dependency.management)
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.graalvm.native.gradle.plugin)
    implementation(libs.spotbugs.gradle.plugin)
}
