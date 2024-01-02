plugins {
    kotlin("jvm") version "1.9.21"

    application
}

group = "io.github.sickandpanik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
    implementation(kotlin("reflect"))
}

application {
    mainClass.set("io.github.sickandpanik.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}