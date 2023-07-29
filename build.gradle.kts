import org.gradle.internal.impldep.com.amazonaws.auth.AWSCredentialsProvider

val ktor_version : String by project
val kgraphql_version : String by project
val logback_version : String by project

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("io.ktor.plugin") version "2.3.2"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.apurebase:kgraphql:$kgraphql_version")
    implementation("com.apurebase:kgraphql-ktor:$kgraphql_version")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("aws.sdk.kotlin:s3:0.25.0-beta")
    implementation("aws.sdk.kotlin:dynamodb:0.28.1-beta")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.8.+")
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
    }
}

ktor {
    docker {
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(
                9000,
                9000,
                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
            )
        ))
    }
}

application {
    mainClass.set("com.server.MainKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
