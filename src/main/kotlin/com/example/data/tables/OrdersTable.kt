package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object OrdersTable: Table("orders") {
    val orderId = integer("order_id").autoIncrement()
    val userId = (integer("user_id") references UsersTable.userId)
    val orderName = varchar("order_name", length = 255)
    val createdAt = varchar("created_at", length = 255)

    override val primaryKey = PrimaryKey(orderId)
}