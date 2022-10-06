package com.example.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val id: Int = -1,
    val userId: Int = -1,
    val orderName: String = ""
)
