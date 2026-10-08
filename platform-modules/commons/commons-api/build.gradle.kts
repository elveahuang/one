plugins {
    id("java-library-conventions")
}

dependencies {
    api(project(":platform-modules:system:system-api"))
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("platform-commons-api")
}
