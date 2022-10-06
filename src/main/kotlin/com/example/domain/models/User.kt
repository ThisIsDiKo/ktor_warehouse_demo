package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = -1,
    val username: String,
    val password: String,
    val salt: String
)