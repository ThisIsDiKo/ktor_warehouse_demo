package com.example.domain.request

@kotlinx.serialization.Serializable
data class ImageMetaInfo(
    val orderId: Int,
    val orderName: String
)
