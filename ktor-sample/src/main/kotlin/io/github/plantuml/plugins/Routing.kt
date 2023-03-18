package io.github.plantuml.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*

fun Application.configureRouting() {

    install(AutoHeadResponse)

    routing {
        get("/") {
            call.respondRedirect("/plantuml-core/raw.html")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
        static("/plantuml-core") {

            files("../docs")
        }
    }
}
