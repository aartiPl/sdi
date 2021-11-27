plugins {
    kotlin("jvm") version "1.6.0"
    `java-library`
    `maven-publish`
    signing
    scala
}

group = "net.igsoft"
version = "0.9.3-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

sourceSets {
    create("javaExample") {
        java.srcDir("src/example/java")
        compileClasspath += sourceSets.getByName("main").output + sourceSets.getByName("main").compileClasspath
        runtimeClasspath += sourceSets.getByName("main").output + sourceSets.getByName("main").runtimeClasspath
    }
    create("scalaExample") {
        withConvention(ScalaSourceSet::class) {
            scala.srcDir("src/example/scala")
            compileClasspath += sourceSets.getByName("main").output + sourceSets.getByName("main").compileClasspath
            runtimeClasspath += sourceSets.getByName("main").output + sourceSets.getByName("main").runtimeClasspath
        }
    }
}

val scalaExampleImplementation: Configuration by configurations.named("scalaExampleImplementation").also {
    it.get().extendsFrom(configurations.getByName("implementation"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "sdi"
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("Simple Dependency Injection")
                description.set("Simple Dependency Injection for Java")
                url.set("https://github.com/aartiPl/sdi/tree/master")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("aartiPl")
                        name.set("Marcin Kuszczak")
                        email.set("aarti@interia.pl")
                    }
                }
                scm {
                    connection.set("scm:git:git://https://github.com/aartiPl/sdi.git")
                    developerConnection.set("scm:git:ssh:https://github.com/aartiPl/sdi.git")
                    url.set("https://github.com/aartiPl/sdi/tree/master")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = project.findProperty("sonatype.user") as String? ?: System.getenv("SONATYPE_USER")
                password = project.findProperty("sonatype.password") as String? ?: System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    reports {
        html.required.set(true)
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}


dependencies {
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("ch.qos.logback:logback-classic:1.2.7")

    scalaExampleImplementation("org.scala-lang:scala3-library_3:3.1.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.openpojo:openpojo:0.9.1")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.7.2")
}
