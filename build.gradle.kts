plugins {
    kotlin("jvm")
    id("maven-publish") // <--- Add this
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"]) // Uses the Java component
            version = "0.1.0-beta01"
        }
    }
}