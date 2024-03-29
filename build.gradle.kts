plugins {
    kotlin ("jvm") version "1.9.20" apply true
    id("io.ktor.plugin") version "2.3.9" apply true
    application
}

group = "me.mudan"
version = "1.0.0"

dependencies {
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.9")
    // 直接访问buildSrc中的kotlin代码
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    val ktorVersion = "2.3.9"
    // ktor使用的HTTP引擎
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    // ktor实现的核心代码
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    // ktor的日志插件
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    // Define the main class for the application.
    mainClass.set("me.mudan.sina.AppKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}