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
    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)

    // Logging
    implementation(libs.logback.classic)

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.43.0") // ✅ 추가
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")

    // Database
    implementation("org.postgresql:postgresql:42.5.4") // ✅ PostgreSQL
    implementation("com.h2database:h2:2.2.224") // ✅ H2 (테스트용)

    // HikariCP (DB Connection Pool)
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Security
    implementation("org.mindrot:jbcrypt:0.4")

    // Coroutines 최신 버전 적용
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // ✅ 최신화

    // 테스트 라이브러리
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.5")
    testImplementation("io.ktor:ktor-server-test-host:2.3.5")

    // MockK
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.7")

    // Exposed 테스트 지원
    testImplementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    testImplementation("com.h2database:h2:2.2.224")

    // Kotlin 테스트 지원
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("ktor.config", "src/test/resources/application.yaml") // ✅ 환경 설정 적용 방식 변경
}
