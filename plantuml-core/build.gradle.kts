plugins {
    application
    java
    eclipse
}

eclipse {
    project {
        comment = "Project plantuml-core-plantuml-core created by Buildship."
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    implementation(fileTree("lib"))
}

application {
    mainClass.set("com.plantuml.wasm.v1.RunInit")
}

tasks.test {
    useJUnitPlatform()
}

tasks.clean {
    dependsOn(tasks.cleanEclipse)
    delete.add(".settings")
}
