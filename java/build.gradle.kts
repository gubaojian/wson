plugins {
    id("java")
}

group = "com.github.gubaojian.wson"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.alibaba:fastjson:1.1.61.android")
    // https://mvnrepository.com/artifact/com.google.flatbuffers/flatbuffers-java
    implementation("com.google.flatbuffers:flatbuffers-java:24.3.25")

    implementation("com.google.protobuf:protobuf-java:3.4.0")
    implementation("com.googlecode.protobuf-java-format:protobuf-java-format:1.4")
    implementation("org.apache.commons:commons-lang3:3.4")

    // https://mvnrepository.com/artifact/junit/junit
    testImplementation("junit:junit:4.13.2")





    //testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //testImplementation("org.junit.jupiter:junit-jupiter")
}

/**
tasks.test {
    useJUnitPlatform()
}*/