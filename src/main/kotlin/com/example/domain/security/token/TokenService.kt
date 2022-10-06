package com.example.domain.security.token

interface TokenService {
    fun generatate(config: TokenConfig, vararg claims: TokenClaim): String
}