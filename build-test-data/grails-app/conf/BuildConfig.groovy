grails.project.work.dir = "target"

grails.project.dependency.resolution = {

    inherits "global"
    log "warn"

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile "dk.brics.automaton:automaton:1.11-8"
    }

    plugins {
        build ":release:2.2.1", ":rest-client-builder:1.0.3", {
            export = false
        }
    }
}
