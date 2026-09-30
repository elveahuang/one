import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("spring-boot-conventions")
}

dependencies {
    // libs
    implementation(rootProject.libs.bundles.springBootServletStarter)
    implementation(rootProject.libs.bundles.springBootAdminServerStarter)
    implementation(rootProject.libs.bundles.springSecurityCoreStarter)
    implementation(rootProject.libs.bundles.logging)
    implementation(rootProject.libs.bundles.storage)
    // modules
    implementation(project(":platform-commons:commons-core"))
}

tasks.named<BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("admin.jar")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    builder = "bellsoft/buildpacks.builder:musl"
    environment = mapOf(
        "BP_LOG_LEVEL" to "debug",
        "BP_JVM_VERSION" to "25",
        "BPL_JVM_CDS_ENABLED" to "true",
        "BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
        "BPE_APPEND_JAVA_TOOL_OPTIONS" to "-XX:+HeapDumpOnOutOfMemoryError",
    )
    bindings = listOf(
        "$rootDir/tools/buildpacks/bindings-remote:/platform/bindings"
    )
    cleanCache = true
    imageName = "boot-admin-server"
}
