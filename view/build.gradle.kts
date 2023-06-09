plugins {
    java
}

group = "up"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation(project(":model"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}