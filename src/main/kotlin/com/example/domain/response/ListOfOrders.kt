package com.example.domain.response

import com.example.domain.models.Order

@kotlinx.serialization.Serializable
data class ListOfOrders(
    val orders: List<OrderInfo>
)
