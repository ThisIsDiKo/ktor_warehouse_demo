package com.example.domain.request

@kotlinx.serialization.Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
