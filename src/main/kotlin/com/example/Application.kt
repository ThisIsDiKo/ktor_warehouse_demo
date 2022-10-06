package com.example

import com.example.data.tables.DatabaseFactory
import com.example.domain.security.hashing.Sha256HashingService
import com.example.domain.security.token.JwtTokenService
import com.example.domain.security.token.TokenConfig
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

fun Application.module() {

    val SECRET = "zZrq0sZK1yt9RJk51RTJ/jeU6WERbvr8nqKMWQJRX1E="

    val hashingService = Sha256HashingService()
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").toString(),
        audience = environment.config.property("jwt.audience").toString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        //expiresIn = 3000000,
        secret = SECRET
    )

    DatabaseFactory.init()
    configureSerialization()
    configureMonitoring()

    configureRouting(
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )

    configureSecurity(tokenConfig)
}
