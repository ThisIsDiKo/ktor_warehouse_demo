package com.example.domain.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderInfo(
    val orderId: Int,
    val orderName: String,
    val username: String,
    val status: String,
    val createdAt: String,
    val comment: String,
    val images: List<String>
)

//data class OrderInfo(
//    val id: Int,
//    val userName: String,
//    val orderName: String,
//    val createdAt: String,
//    val images: List<String>
//)
