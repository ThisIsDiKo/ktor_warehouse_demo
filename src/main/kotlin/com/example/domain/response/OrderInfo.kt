package com.example.domain.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderInfo(
    val id: Int,
    val userName: String,
    val orderName: String,
    val createdAt: String,
    val images: List<String>
)
