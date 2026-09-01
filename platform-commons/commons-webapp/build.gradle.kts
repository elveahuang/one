import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("spring-boot-conventions")
}

dependencies {
    implementation(libs.bundles.springBootCore)
    implementation(libs.bundles.springBootServletStarter)
    implementation(libs.bundles.springBootArthasStarter)
    implementation(libs.bundles.springAiCore)
    implementation(libs.bundles.springAiReader)
    implementation(libs.bundles.springAiRagCore)
    implementation(libs.bundles.springAiCoreStarter)
    implementation(libs.bundles.springAiMcpCore)
    implementation(libs.bundles.springAiMcpStarter)
    implementation(libs.bundles.springAiSessionStarter)
    implementation(libs.bundles.springAiAgenticCore)
    implementation(libs.bundles.springSecurityCore)
    implementation(libs.bundles.springSecurityCoreStarter)
    implementation(libs.bundles.springSecurityClientStarter)
    implementation(libs.bundles.springDocServletStarter)
    implementation(libs.bundles.redis)
    implementation(libs.bundles.redisStarter)
    implementation(libs.bundles.mybatis)
    implementation(libs.bundles.mybatisStarter)
    implementation(libs.bundles.hibernate)
    implementation(libs.bundles.hibernateStarter)
    implementation(libs.bundles.rabbit)
    implementation(libs.bundles.rabbitStarter)
    implementation(libs.bundles.elastic)
    implementation(libs.bundles.elasticStarter)
    implementation(libs.bundles.embabelAgentCore)
    implementation(libs.bundles.embabelAgentStarter)
    implementation(libs.bundles.agentScopeCore)
    implementation(libs.bundles.agentScopeStarter)
    implementation(libs.bundles.storage)
    implementation(libs.bundles.ip)
    implementation(libs.bundles.im)
    implementation(libs.bundles.ai)
    implementation(libs.bundles.pdf)
    implementation(libs.bundles.image)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.selenium)
    implementation(libs.bundles.webjars)
    // modules
    implementation(project(":platform-commons:commons-core-starter"))
    implementation(project(":platform-commons:commons-javacv"))
}

tasks.register<Delete>("clearLibs") {
    description = "清理依赖"
    delete(layout.buildDirectory.dir("libs/libs-internal"));
    delete(layout.buildDirectory.dir("libs/libs-external"));
}

tasks.register<Copy>("copyInternalLibs") {
    description = "拷贝内部依赖"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath)
    include("**/platform-*.jar")
    into(layout.buildDirectory.dir("libs/libs-internal"))
}

tasks.register<Copy>("copyExternalLibs") {
    description = "拷贝外部依赖"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath)
    exclude("**/platform-*.jar")
    into(layout.buildDirectory.dir("libs/libs-external"))
}

tasks.named<BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("clearLibs")
    dependsOn("copyExternalLibs")
    dependsOn("copyInternalLibs")
    exclude("*.jar")
    manifest {
        attributes["Manifest-Version"] = 1.0
        attributes["Class-Path"] = configurations.runtimeClasspath.get().files.onEach {
            println(it.name)
        }.joinToString(" ") {
            if (it.name.startsWith("platform-")) "./libs-internal/" + it.name else "./libs-external/" + it.name
        }
    }
    archiveFileName.set("webapp.jar")
}
