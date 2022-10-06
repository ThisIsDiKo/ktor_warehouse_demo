package com.example.domain.models

import com.example.data.tables.ImagesTable
import com.example.data.tables.ImagesTable.autoIncrement
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Int = -1,
    val orderId: Int,
    val name: String,
    val uri: String
)
