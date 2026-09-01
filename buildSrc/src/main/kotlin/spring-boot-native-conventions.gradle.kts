import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("spring-boot-conventions")
    id("org.graalvm.buildtools.native")
}
