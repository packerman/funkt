plugins {
    kotlin("jvm") version "1.3.61"
}

group = "packermann"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
