// 基础配置
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}

rootProject.name = "one"
// -----------------------------------------------------------------------
// 公共模块
// -----------------------------------------------------------------------
include("platform-commons:commons-core")
include("platform-commons:commons-core-starter")
include("platform-commons:commons-javacv")
include("platform-commons:commons-console")
include("platform-commons:commons-native")
include("platform-commons:commons-webapp")
// -----------------------------------------------------------------------
// 应用模块
// -----------------------------------------------------------------------
include("platform-modules:commons:commons-api")
include("platform-modules:commons:commons-starter")
include("platform-modules:system:system-api")
include("platform-modules:system:system-impl")
include("platform-modules:system:system-security")
// -----------------------------------------------------------------------
// 应用服务
// -----------------------------------------------------------------------
include("platform-services:admin-server")
include("platform-services:app-server")
