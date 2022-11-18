package com.example.domain.request

@kotlinx.serialization.Serializable
data class CreateOrderRequest(
    val orderName: String,
    val comment: String
)
