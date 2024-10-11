plugins {
    id("java")
}

group = "com.github.gubaojian.wson"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://maven.aliyun.com/repository/central/")
    maven(url = "https://maven.aliyun.com/repository/public/")
    maven(url = "https://maven.aliyun.com/repository/google/")
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin/")
}

dependencies {

    implementation("com.alibaba.fastjson2:fastjson2:2.0.52")
    // https://mvnrepository.com/artifact/com.google.flatbuffers/flatbuffers-java
    implementation("com.google.flatbuffers:flatbuffers-java:24.3.25")

    implementation("com.google.protobuf:protobuf-java:3.4.0")
    implementation("com.googlecode.protobuf-java-format:protobuf-java-format:1.4")
    implementation("org.apache.commons:commons-lang3:3.4")
    implementation("org.apache.fury:fury-core:0.7.0")

    // https://mvnrepository.com/artifact/org.msgpack/msgpack-core
    implementation("org.msgpack:msgpack-core:0.9.8")


    // https://mvnrepository.com/artifact/org.msgpack/jackson-dataformat-msgpack
    implementation("org.msgpack:jackson-dataformat-msgpack:0.9.8")


    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")


    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-blackbird
    implementation("com.fasterxml.jackson.module:jackson-module-blackbird:2.18.0")



    // https://mvnrepository.com/artifact/junit/junit
    testImplementation("junit:junit:4.13.2")


    // https://mvnrepository.com/artifact/com.github.luben/zstd-jni
    implementation("com.github.luben:zstd-jni:1.5.6-5")


    // https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-core
    testImplementation("org.openjdk.jmh:jmh-core:1.37")


    // https://mvnrepository.com/artifact/cn.hutool/hutool-all
    testImplementation("cn.hutool:hutool-all:5.8.32")


    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    testImplementation("org.apache.commons:commons-compress:1.27.1")


    // https://mvnrepository.com/artifact/org.xerial.snappy/snappy-java
    testImplementation("org.xerial.snappy:snappy-java:1.1.10.7")


    // https://mvnrepository.com/artifact/io.airlift/aircompressor
    testImplementation("io.airlift:aircompressor:2.0.2")


    // https://mvnrepository.com/artifact/org.openjdk.jmh/jmh-generator-annprocess
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.37")



    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    // https://mvnrepository.com/artifact/com.github.luben/zstd-jni
    implementation("com.github.luben:zstd-jni:1.5.6-5")





    //testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //testImplementation("org.junit.jupiter:junit-jupiter")
}

/**
tasks.test {
    useJUnitPlatform()
}*/