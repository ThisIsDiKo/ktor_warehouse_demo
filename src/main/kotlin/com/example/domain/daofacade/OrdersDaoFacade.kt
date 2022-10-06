package com.example.domain.daofacade

import com.example.domain.models.Order


interface OrdersDaoFacade {
    suspend fun getAllOrders(): List<Order>?
    suspend fun getOrdersByUserId(userId: Int): List<Order>?
    suspend fun getOrderByOrderName(orderName: String): Order?
    suspend fun getOrderByOrderId(orderId: Int): Order?
    suspend fun insertOrder(order: Order): Order?
    suspend fun deleteOrder(orderId: Int): Boolean
}