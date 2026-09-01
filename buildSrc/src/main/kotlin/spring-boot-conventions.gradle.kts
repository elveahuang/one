import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("java-library-conventions")
    id("org.springframework.boot")
}
