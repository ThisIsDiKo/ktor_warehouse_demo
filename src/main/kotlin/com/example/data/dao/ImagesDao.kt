package com.example.data.dao

import com.example.data.tables.DatabaseFactory.dbQuery
import com.example.data.tables.ImagesTable
import com.example.data.tables.OrdersTable
import com.example.domain.daofacade.ImagesDaoFacade
import com.example.domain.models.Image
import com.example.domain.models.Order
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

val imagesDao: ImagesDaoFacade = ImagesDao()

class ImagesDao: ImagesDaoFacade {
    override suspend fun getImagesByOrderId(orderId: Int): List<Image>? = dbQuery{
        ImagesTable.select { ImagesTable.orderId eq orderId }.map(::resultRowToImage)
    }

    override suspend fun getImageById(imageId: Int): Image? = dbQuery{
        ImagesTable.select{
            ImagesTable.imageId eq imageId
        }.map(::resultRowToImage).singleOrNull()
    }
    override suspend fun getImageByName(imageName: String): Image? = dbQuery{
        ImagesTable.select{
            ImagesTable.imageName eq imageName
        }.map(::resultRowToImage).singleOrNull()
    }
    override suspend fun insertImage(image: Image): Image? = dbQuery{
        if (getImageByName(imageName = image.name) != null) return@dbQuery null

        val insertStatement = ImagesTable.insert {
            it[ImagesTable.imageName] = image.name
            it[ImagesTable.orderId] = image.orderId
            it[ImagesTable.uri] = image.uri
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToImage)
    }

    override suspend fun deleteImage(imageName: String): Boolean = dbQuery{
        ImagesTable.deleteWhere{ImagesTable.imageName eq imageName} > 0
    }

    private fun resultRowToImage(row: ResultRow) = Image(
        id = row[ImagesTable.imageId],
        orderId = row[ImagesTable.orderId],
        name = row[ImagesTable.imageName],
        uri = row[ImagesTable.uri]
    )
}