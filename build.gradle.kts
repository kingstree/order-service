plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bookshop"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly ("org.flywaydb:flyway-core")
    runtimeOnly ("org.postgresql:r2dbc-postgresql")
    runtimeOnly ("org.springframework:spring-jdbc")
    runtimeOnly("org.flywaydb:flyway-database-postgresql") //Flyway 10.x부터는 flyway-core 만으로는 각 DB를 지원하지 않고, 별도 아티팩트를 추가해야 해요.
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
