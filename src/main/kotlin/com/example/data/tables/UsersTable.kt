package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object UsersTable: Table("users") {
    val userId = integer("user_id").autoIncrement()
    val username = varchar("username", length = 255)
    val password = varchar("password", length = 255)
    val salt = varchar("salt", length = 255)

    override val primaryKey = PrimaryKey(userId)
}