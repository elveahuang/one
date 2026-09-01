plugins {
    id("java-library-conventions")
}

dependencies {
    // libs
    implementation(libs.spring.ai.rag)
    implementation(libs.bundles.springAiMcpCore)
    implementation(libs.bundles.springCloudCore)
    implementation(libs.bundles.excel)
    implementation(libs.bundles.im)
    // modules
    api(project(":platform-commons:commons-core"))
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("platform-system-api")
}
