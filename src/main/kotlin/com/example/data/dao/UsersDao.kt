package com.example.data.dao

import com.example.data.tables.DatabaseFactory.dbQuery
import com.example.data.tables.UsersTable
import com.example.domain.daofacade.UsersDaoFacade
import com.example.domain.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

val usersDao: UsersDaoFacade = UsersDao()

class UsersDao: UsersDaoFacade {
    override suspend fun getUserByUsername(username: String): User? = dbQuery{
        UsersTable.select{
            UsersTable.username eq username
        }.map(::resultRowToUser).singleOrNull()
    }

    override suspend fun getUserById(id: Int): User? = dbQuery{
        UsersTable.select{
            UsersTable.userId eq id
        }.map(::resultRowToUser).singleOrNull()
    }

    override suspend fun insertUser(user: User): User? = dbQuery{
        if (getUserByUsername(user.username) != null) return@dbQuery null

        val insertStatement = UsersTable.insert {
            it[UsersTable.username] = user.username
            it[UsersTable.password] = user.password
            it[UsersTable.salt] = user.salt
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[UsersTable.userId],
        username = row[UsersTable.username],
        password = row[UsersTable.password],
        salt = row[UsersTable.salt],
    )
}

