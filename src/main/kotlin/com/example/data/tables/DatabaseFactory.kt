package com.example.data.tables

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(){
        val driverClassName = "com.mysql.cj.jdbc.Driver"
        //val jdbcUrl = "jdbc:mysql://172.16.1.54:3306/warehouse"
        val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/warehouse"
        val user = "ServerUser"
        val password = "UserPassword22"

        val database = Database.connect(
            url = jdbcUrl,
            driver = driverClassName,
            user = user,
            password = password
        )

//        transaction(database) {
//            SchemaUtils.create(UsersTable)
//        }
    }

    suspend fun <T> dbQuery (block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}