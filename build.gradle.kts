plugins {
    id ("org.jetbrains.kotlin.jvm") version "1.9.20" apply true
    application

}

group = "me.mudan"
version = "1.0.0"

dependencies {
    // 直接访问buildSrc中的kotlin代码
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("org.jsoup:jsoup:1.15.3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "17"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "17"
}

application {
    // Define the main class for the application.
    mainClass.set("me.mudan.sina.AppKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}