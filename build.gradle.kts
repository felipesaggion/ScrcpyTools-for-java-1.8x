import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("edu.sc.seis.launch4j") version "3.0.5"
    `maven-publish`
}

group = "br.com.saggion"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0-RC2")
    implementation("commons-io:commons-io:2.16.1")
}

application {
    mainModule.set("kotlin.scrcpytools")
    mainClass.set("br.com.saggion.scrcpytools.App")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

launch4j {
    mainClassName = "br.com.saggion.scrcpytools.App"
    icon = "$projectDir/src/main/resources/br/com/saggion/scrcpytools/icon.ico"
    version = project.version.toString()
    jreMaxVersion = "1.8.0_202"
    messagesInstanceAlreadyExists = "The application is already running"
    setJarTask(tasks.shadowJar.get())
}

tasks.getByName<Jar>("jar") {
    enabled = true
    manifest {
        attributes["Main-Class"] = "br.com.saggion.scrcpytools.App"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.shadowJar {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
}
