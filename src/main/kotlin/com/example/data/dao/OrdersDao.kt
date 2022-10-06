package com.example.data.dao

import com.example.data.tables.DatabaseFactory.dbQuery
import com.example.data.tables.OrdersTable
import com.example.domain.daofacade.OrdersDaoFacade
import com.example.domain.models.Order
import org.jetbrains.exposed.sql.*

val ordersDao: OrdersDaoFacade = OrdersDao()

class OrdersDao: OrdersDaoFacade {
    override suspend fun getAllOrders(): List<Order>? = dbQuery{
        OrdersTable.selectAll().map(::resultRowToOrder)
    }

    override suspend fun getOrdersByUserId(userId: Int): List<Order>? = dbQuery{
       OrdersTable.select {
            OrdersTable.userId eq userId
        }.map(::resultRowToOrder)
    }

    override suspend fun getOrderByOrderName(orderName: String): Order? = dbQuery{
        OrdersTable.select {
            OrdersTable.orderName eq orderName
        }.map(::resultRowToOrder).singleOrNull()
    }

    override suspend fun getOrderByOrderId(orderId: Int): Order? = dbQuery{
        OrdersTable.select {
            OrdersTable.orderId eq orderId
        }.map(::resultRowToOrder).singleOrNull()
    }

    override suspend fun insertOrder(order: Order): Order? = dbQuery{
        if (getOrderByOrderName(orderName = order.orderName) != null) return@dbQuery null

        val insertStatement = OrdersTable.insert {
            it[OrdersTable.orderName] = order.orderName
            it[OrdersTable.userId] = order.userId
            it[OrdersTable.createdAt] = order.createdAt
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToOrder)
    }

    override suspend fun deleteOrder(orderId: Int): Boolean = dbQuery{
        OrdersTable.deleteWhere { OrdersTable.orderId eq orderId } > 0
    }

    private fun resultRowToOrder(row: ResultRow) = Order(
        id = row[OrdersTable.orderId],
        userId = row[OrdersTable.userId],
        orderName = row[OrdersTable.orderName],
        createdAt = row[OrdersTable.createdAt],
    )
}