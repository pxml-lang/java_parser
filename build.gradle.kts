plugins {
    // add support for building a CLI application in Java.
    application
}

version = "0.8.0"

repositories {
    jcenter()
}

dependencies {
    testImplementation ( "org.junit.jupiter:junit-jupiter-api:5.4.2" )
    testRuntimeOnly ( "org.junit.jupiter:junit-jupiter-engine:5.4.2" )
}

java {
    toolchain {
        languageVersion.set ( JavaLanguageVersion.of(11) )
    }
}

tasks.withType<CreateStartScripts> {
    applicationName = "pxml";
}

application {
    mainClass.set ( "dev.pxml.core.PXMLConverter" )
    // enable assertions
    applicationDefaultJvmArgs = listOf ( "-ea" )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
