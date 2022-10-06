package com.example.data.tables

import com.example.data.tables.OrdersTable.references
import com.mysql.cj.x.protobuf.MysqlxCrud.Order
import org.jetbrains.exposed.sql.Table

object ImagesTable: Table("images") {
    val imageId = integer("image_id").autoIncrement()
    val orderId = (integer("order_id") references OrdersTable.orderId)
    val imageName = varchar("name", length = 255)
    val uri = varchar("uri", length = 255)

    override val primaryKey = PrimaryKey(imageId)
}