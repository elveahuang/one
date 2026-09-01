import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("spring-boot-conventions")
}

dependencies {
    // libs
    implementation(rootProject.libs.bundles.springAiCore)
    implementation(rootProject.libs.bundles.springAiReader)
    implementation(rootProject.libs.bundles.springAiCoreStarter)
    implementation(rootProject.libs.bundles.springAiRagCore)
    implementation(rootProject.libs.bundles.springAiMcpCore)
    implementation(rootProject.libs.bundles.springAiMcpStarter)
    implementation(rootProject.libs.bundles.springBootServletStarter)
    implementation(rootProject.libs.bundles.springSecurityCoreStarter)
    implementation(rootProject.libs.bundles.springSecurityAuthorizationServerStarter)
    implementation(rootProject.libs.bundles.springSecurityResourceServerStarter)
    implementation(rootProject.libs.bundles.springBootAdminClientStarter)
    implementation(rootProject.libs.bundles.springCloudCoreStarter)
    implementation(rootProject.libs.bundles.springDocServletStarter)
    implementation(rootProject.libs.bundles.elastic)
    implementation(rootProject.libs.bundles.elasticStarter)
    implementation(rootProject.libs.bundles.rabbit)
    implementation(rootProject.libs.bundles.rabbitStarter)
    implementation(rootProject.libs.bundles.redis)
    implementation(rootProject.libs.bundles.redisStarter)
    implementation(rootProject.libs.bundles.quartzStarter)
    implementation(rootProject.libs.bundles.mybatisStarter)
    implementation(rootProject.libs.bundles.websocketStarter)
    implementation(rootProject.libs.bundles.selenium)
    implementation(rootProject.libs.bundles.im)
    implementation(rootProject.libs.bundles.ai)
    implementation(rootProject.libs.bundles.logging)
    implementation(rootProject.libs.bundles.storage)
    implementation(project(":platform-commons:commons-javacv"))
    // modules
    implementation(project(":platform-modules:commons:commons-starter"))
    implementation(project(":platform-modules:system:system-impl"))
    implementation(project(":platform-modules:system:system-security"))
}

tasks.named<BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("app.jar")
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
    imageName = "boot-app-server"
}

tasks.withType<ProcessAot> {
    args("--spring.profiles.active=native")
}
