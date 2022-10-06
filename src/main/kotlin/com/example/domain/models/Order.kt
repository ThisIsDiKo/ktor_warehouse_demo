package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int = -1,
    val userId: Int,
    val orderName: String,
    val createdAt: String
)
