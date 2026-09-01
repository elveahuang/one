plugins {
    id("java-library-conventions")
}

dependencies {
    // libs
    api(libs.bundles.springBootCore)
    annotationProcessor(libs.bundles.springBootAnnotationProcessor)
    compileOnly(libs.bundles.springBootServletStarter)
    compileOnly(libs.bundles.springBootReactiveStarter)
    compileOnly(libs.bundles.springAiCore)
    compileOnly(libs.bundles.springAiBaseStarter)
    compileOnly(libs.bundles.springAiCoreStarter)
    compileOnly(libs.bundles.springAiRagCore)
    compileOnly(libs.bundles.springAiRagStarter)
    compileOnly(libs.bundles.springAiAgenticCore)
    compileOnly(libs.bundles.springCloudCore)
    compileOnly(libs.bundles.springSecurityCore)
    compileOnly(libs.bundles.springSecurityCoreStarter)
    compileOnly(libs.bundles.mybatis)
    compileOnly(libs.bundles.mybatisStarter)
    compileOnly(libs.bundles.redis)
    compileOnly(libs.bundles.redisStarter)
    compileOnly(libs.bundles.hibernate)
    compileOnly(libs.bundles.hibernateStarter)
    compileOnly(libs.bundles.elastic)
    compileOnly(libs.bundles.elasticStarter)
    compileOnly(libs.bundles.rabbit)
    compileOnly(libs.bundles.rabbitStarter)
    compileOnly(libs.bundles.quartz)
    compileOnly(libs.bundles.quartzStarter)
    compileOnly(libs.bundles.websocket)
    compileOnly(libs.bundles.websocketStarter)
    compileOnly(libs.bundles.office)
    compileOnly(libs.bundles.officeStarter)
    compileOnly(libs.bundles.storage)
    compileOnly(libs.bundles.sharding)
    compileOnly(libs.bundles.selenium)
    compileOnly(libs.bundles.im)
    compileOnly(libs.bundles.sms)
    compileOnly(libs.bundles.ip)
    compileOnly(libs.bundles.telegram)
    compileOnly(libs.bundles.html)
    compileOnly(libs.bundles.ai)
    compileOnly(libs.bundles.pdf)
    // modules
    api(project(":platform-commons:commons-core"))
    compileOnly(project(":platform-commons:commons-javacv"))
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("platform-commons-core-starter")
}
