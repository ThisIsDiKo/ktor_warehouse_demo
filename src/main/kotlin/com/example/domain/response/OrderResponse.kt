package com.example.domain.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val id: Int,
    val userId: Int,
    val orderName: String,
    val createdAt: String
)
