plugins {
    id("java-library-conventions")
}

dependencies {
    // libs
    implementation(libs.bundles.springCore)
    implementation(libs.bundles.springAiCore)
    implementation(libs.bundles.springAiCoreStarter)
    implementation(libs.bundles.springAiMcpCore)
    implementation(libs.bundles.springAiMcpStarter)
    implementation(libs.bundles.springBootCore)
    implementation(libs.bundles.springSecurityCore)
    implementation(libs.bundles.springSecurityCoreStarter)
    implementation(libs.bundles.redis)
    implementation(libs.bundles.elastic)
    implementation(libs.bundles.websocket)
    implementation(libs.bundles.mybatis)
    implementation(libs.bundles.rabbit)
    implementation(libs.bundles.quartz)
    implementation(libs.bundles.excel)
    implementation(libs.bundles.im)
    implementation(libs.bundles.sms)
    implementation(libs.bundles.ip)
    implementation(libs.bundles.image)
    // modules
    api(project(":platform-commons:commons-core"))
    api(project(":platform-modules:system:system-api"))
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("platform-system-impl")
}
