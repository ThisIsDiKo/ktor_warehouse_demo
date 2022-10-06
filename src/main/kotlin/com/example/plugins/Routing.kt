package com.example.plugins

import com.example.domain.routes.orders
import com.example.domain.routes.users
import com.example.domain.security.hashing.HashingService
import com.example.domain.security.token.TokenConfig
import com.example.domain.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting(
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    install(AutoHeadResponse)
    

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }

        orders()
        users(
            hashingService = hashingService,
            tokenService = tokenService,
            config = tokenConfig
        )
    }
}
