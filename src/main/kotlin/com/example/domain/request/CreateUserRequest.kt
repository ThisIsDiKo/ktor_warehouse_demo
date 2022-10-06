package com.example.domain.request

@kotlinx.serialization.Serializable
data class CreateUserRequest(
    val username: String,
    val password: String
)
