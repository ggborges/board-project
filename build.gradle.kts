plugins {
    id("java")
}

group = "borges.gustavo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // migração de base de dados (alternativa ao FlyWay)
    implementation("org.liquibase:liquibase-core:4.29.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.projectlombok:lombok:1.18.34")

    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.test {
    useJUnitPlatform()
}