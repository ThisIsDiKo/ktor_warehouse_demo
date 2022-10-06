package com.example.domain.routes

import com.example.data.dao.usersDao
import com.example.domain.models.User
import com.example.domain.request.CreateUserRequest
import com.example.domain.request.LoginRequest
import com.example.domain.response.LoginResponse
import com.example.domain.security.hashing.HashingService
import com.example.domain.security.hashing.SaltedHash
import com.example.domain.security.token.TokenClaim
import com.example.domain.security.token.TokenConfig
import com.example.domain.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.users(
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig,
){
    route("user"){
        post("signup"){
            val createUserRequest = kotlin.runCatching { call.receiveNullable<CreateUserRequest>() }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Incorrect object")

            if(createUserRequest.username.isBlank() || createUserRequest.password.isBlank()){
                return@post call.respond(HttpStatusCode.BadRequest, "Field is blank")
            }
            if(createUserRequest.password.length < 8){
                return@post call.respond(HttpStatusCode.BadRequest, "Password is short")
            }
            if(createUserRequest.username.length < 6){
                return@post call.respond(HttpStatusCode.BadRequest, "Username is short")
            }

            val saltedHash = hashingService.generateSaltedHash(createUserRequest.password)

            val user = User(
                username = createUserRequest.username,
                password = saltedHash.hash,
                salt = saltedHash.salt
            )

            val createdUser = usersDao.insertUser(user) ?: return@post call.respond(HttpStatusCode.BadRequest, "User already exists")

            call.respond(HttpStatusCode.OK, createdUser)
        }

        post("login"){
            val loginRequest = kotlin.runCatching { call.receiveNullable<LoginRequest>() }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Incorrect Login object")

            if (loginRequest.username.isBlank()) return@post call.respond(HttpStatusCode.BadRequest, "Empty username")
            if (loginRequest.password.isBlank()) return@post call.respond(HttpStatusCode.BadRequest, "Empty password")

            val user = usersDao.getUserByUsername(loginRequest.username) ?: return@post call.respond(HttpStatusCode.BadRequest, "Incorrect username")

            val isValidPassword = hashingService.verify(
                value = loginRequest.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )

            if (!isValidPassword){
                return@post call.respond(HttpStatusCode.Unauthorized, "Wrong password")
            }

            val token = tokenService.generatate(
                config = config,
                TokenClaim(
                    name = "userId",
                    value = user.id.toString()
                ),
                TokenClaim(
                    name = "username",
                    value = user.username
                )
            )

            call.respond(HttpStatusCode.OK, LoginResponse(token = token))
        }
    }
}