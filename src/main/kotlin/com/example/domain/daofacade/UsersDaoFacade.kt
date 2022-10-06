package com.example.domain.daofacade

import com.example.domain.models.User

interface UsersDaoFacade {
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserById(id: Int): User?
    suspend fun insertUser(user: User): User?
}