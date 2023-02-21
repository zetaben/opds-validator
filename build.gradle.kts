plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.relaxng:jing:20220510")
    implementation("org.json:json:20220924")
}

application {
    mainClass.set("com.feedbooks.opds.Validator")
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
        resources {
            srcDirs("res")
        }
    }
}