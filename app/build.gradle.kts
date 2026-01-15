plugins {
    kotlin("jvm") version "2.0.21"
    id("maven-publish")
}

group = "com.digia"
version = "1.0.0-beta.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    
    // JSON processing
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.com.ibm.icu.icu4j) // ICU4J for advanced number formatting
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}

// Configure source sets for Kotlin JVM
sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
    }
    test {
        kotlin.srcDirs("src/test/kotlin")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "com.digia"
            artifactId = "digiaExpr"
            version = "1.0.0-beta.1"
            
            pom {
                name.set("Digia Expression Evaluator")
                description.set("A pure Kotlin expression evaluation library")
                url.set("https://github.com/Digia-Technology-Private-Limited/digia_expr_android")
            }
        }
    }
}