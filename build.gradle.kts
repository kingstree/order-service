plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.bookshop"
version = "0.0.1-SNAPSHOT"
extra.set("testcontainersVersion", "1.19.8")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}
dependencyManagement {// 책에서는 없으나... 클라우드 디펜던시가 필요했음
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.1")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")

    }
}

dependencies {

    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation ("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly ("org.flywaydb:flyway-core")
    runtimeOnly ("org.postgresql:r2dbc-postgresql")
    runtimeOnly ("org.springframework:spring-jdbc")
    runtimeOnly("org.flywaydb:flyway-database-postgresql") //Flyway 10.x부터는 flyway-core 만으로는 각 DB를 지원하지 않고, 별도 아티팩트를 추가해야 해요.

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-java-tiny:0.0.46")
    //imagePlatform.set("linux/arm64")
    imageName.set(project.name)
    //imageName.set("ghcr.io/kingstree/${project.name}:latest")   // ★ 레지스트리·계정 포함
    environment.put("BP_JVM_VERSION", "17")

    docker {
        publishRegistry {
            username = project.findProperty("registryFUsername") as String?
            password = project.findProperty("registryToken") as String?
            url = project.findProperty("registryUrl") as String?
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
