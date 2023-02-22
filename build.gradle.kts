plugins {
    java
    application
    libs.plugins.shadow
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jing)
    implementation(libs.json)
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