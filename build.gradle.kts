plugins {
    base
    eclipse
}

eclipse {
    project {
        comment = "Project plantuml-core-plantuml-core created by Buildship."
    }
}

tasks.clean {
    dependsOn(tasks.cleanEclipse)
    delete.add(".settings")
}