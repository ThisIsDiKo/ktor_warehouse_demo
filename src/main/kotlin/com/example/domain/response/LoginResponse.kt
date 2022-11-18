package com.example.domain.response

@kotlinx.serialization.Serializable
data class LoginResponse(
    val username: String,
    val token: String
)
