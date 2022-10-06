package com.example.domain.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
