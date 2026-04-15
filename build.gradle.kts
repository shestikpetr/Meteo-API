plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.jmix") version "2.8.0"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.shestikpetr"
version = "0.0.1-SNAPSHOT"
description = "MeteoAPI"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://global.repo.jmix.io/repository/public")
    }
}

jmix {
    bomVersion = "2.8.0"
}

dependencyManagement {
    imports {
        mavenBom("io.jmix:jmix-bom:2.8.0")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // Jmix (JPA-провайдер - EclipseLink, Hibernate не используется)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude(group = "org.hibernate.orm", module = "hibernate-core")
    }

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Jmix (JPA-провайдер - EclipseLink, Hibernate не используется)
    implementation("io.jmix.core:jmix-core-starter")
    implementation("io.jmix.data:jmix-eclipselink-starter")
    implementation("io.jmix.flowui:jmix-flowui-starter")
    implementation("io.jmix.security:jmix-security-starter")
    implementation("io.jmix.security:jmix-security-flowui-starter")
    implementation("io.jmix.localfs:jmix-localfs-starter")

    // JWT для REST API
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    runtimeOnly("com.mysql:mysql-connector-j")

    // Flyway - миграции схемы (вместо дефолтного Jmix Liquibase)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Миграции Flyway лежат в /migrations/ (корень проекта),
// при сборке копируются в classpath:db/migration - где Flyway их и ищет
tasks.processResources {
    from("migrations") {
        into("db/migration")
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("1.3.1").editorConfigOverride(
            mapOf(
                "ktlint_standard_property-naming" to "disabled",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.3.1")
    }
}
