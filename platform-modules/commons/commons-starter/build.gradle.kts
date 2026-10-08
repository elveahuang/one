plugins {
    id("java-library-conventions")
}

dependencies {
    // libs
    api(libs.bundles.springSecurityResourceServerStarter)
    // modules
    api(project(":platform-commons:commons-core-starter"))
    api(project(":platform-modules:commons:commons-api"))
    api(project(":platform-modules:system:system-api"))
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("platform-commons-starter")
}
