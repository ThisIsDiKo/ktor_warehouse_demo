package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureSecurity(config: TokenConfig) {
    
    authentication {
            jwt {
                //val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
                realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(config.secret))
                        .withAudience(config.audience)
                        .withIssuer(config.issuer)
                        .build()
                )
                validate { credential ->
                    println("Trying to validate ${credential.issuer}")
                    if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
                }
            }
        }

}
