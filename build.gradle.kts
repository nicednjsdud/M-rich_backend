
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.wyc"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    // postgres
    implementation("org.postgresql:postgresql:42.5.4")
    // hikari
    implementation("com.zaxxer:HikariCP:5.0.1")
    // BCrypt
    implementation("org.mindrot:jbcrypt:0.4")
    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // JUnit 5 (최신 버전 사용)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // Ktor 테스트 라이브러리
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.5")
    testImplementation("io.ktor:ktor-server-test-host:2.3.5")

    // Exposed ORM 테스트 지원
    testImplementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    testImplementation("com.h2database:h2:2.2.224") // 테스트용 DB

    // Kotlin 테스트 지원
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("config.file", "src/test/resources/application.yaml")
}
