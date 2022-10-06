package com.example.domain.routes

import com.example.data.dao.imagesDao
import com.example.data.dao.ordersDao
import com.example.data.dao.usersDao
import com.example.data.tables.ImagesTable
import com.example.data.tables.OrdersTable
import com.example.data.tables.UsersTable
import com.example.domain.models.Image
import com.example.domain.models.Order
import com.example.domain.request.ImageMetaInfo
import com.example.domain.request.OrderRequest
import com.example.domain.response.ListOfOrders
import com.example.domain.response.OrderInfo
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.Part

private val PATH = "/uploads"

fun Route.orders(){
    authenticate {
        route("orders"){

            //Users.deleteWhere{ Users.name like "%thing"}

            get("test"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                val username = principal?.getClaim("username", String::class)

                call.respond(HttpStatusCode.OK, "Your id is $userId, your name is $username")
            }

            get("all") {
                //val orders = ordersDao.getAllOrders() ?: return@get call.respond(HttpStatusCode.NotFound)
                val orders = mutableListOf<OrderInfo>()

                transaction {
                    (OrdersTable innerJoin UsersTable).selectAll().forEach {
                        //val l = ImagesTable.select { ImagesTable.orderId eq it[OrdersTable.orderId] }.map{image -> image[ImagesTable.imageName] }
                        val o = OrderInfo(
                            id = it[OrdersTable.orderId],
                            userName = it[UsersTable.username],
                            orderName = it[OrdersTable.orderName],
                            createdAt = it[OrdersTable.createdAt],
                            images = emptyList()
                        )
//                        println("$o")
//                        println("${it[OrdersTable.orderName]} created by ${it[UsersTable.username]}")
                        orders.add(o)
                    }
                }


                call.respond(HttpStatusCode.OK, ListOfOrders(orders=orders))
            }

//            get("user/{id}"){
//                val userId = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
//                val orders = ordersDao.getOrdersByUserId(userId) ?: return@get call.respond(HttpStatusCode.NotFound)
//
//                //call.respond(HttpStatusCode.OK, ListOfOrders(orders=orders))
//            }

            get("search/{name}"){
                val orderName = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                val orders = mutableListOf<OrderInfo>()

                transaction {
                    (OrdersTable innerJoin UsersTable).select(OrdersTable.orderName like "$orderName%").forEach {
                        //val l = ImagesTable.select { ImagesTable.orderId eq it[OrdersTable.orderId] }.map{image -> image[ImagesTable.imageName] }
                        val o = OrderInfo(
                            id = it[OrdersTable.orderId],
                            userName = it[UsersTable.username],
                            orderName = it[OrdersTable.orderName],
                            createdAt = it[OrdersTable.createdAt],
                            images = emptyList()
                        )
                        orders.add(o)
                    }
                }


                call.respond(HttpStatusCode.OK, ListOfOrders(orders=orders))
            }

            get("user"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)?.toInt()
                val orders = mutableListOf<OrderInfo>()

                transaction {
                    (OrdersTable innerJoin UsersTable).select{OrdersTable.userId eq userId!!}.forEach {
                        //val l = ImagesTable.select { ImagesTable.orderId eq it[OrdersTable.orderId] }.map{image -> image[ImagesTable.imageName] }
                        val o = OrderInfo(
                            id = it[OrdersTable.orderId],
                            userName = it[UsersTable.username],
                            orderName = it[OrdersTable.orderName],
                            createdAt = it[OrdersTable.createdAt],
                            images = emptyList()
                        )
//                        println("$o")
//                        println("${it[OrdersTable.orderName]} created by ${it[UsersTable.username]}")
                        orders.add(o)
                    }
                }


                call.respond(HttpStatusCode.OK, ListOfOrders(orders=orders))
            }

            get("id/{id}"){
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)

                val order = ordersDao.getOrderByOrderId(id) ?: return@get call.respond(HttpStatusCode.NotFound, "No order with id $id")

                val user = usersDao.getUserById(order.userId) ?: return@get call.respond(HttpStatusCode.NotFound, "No user")

                val orderImages = imagesDao.getImagesByOrderId(order.id)
                val imagesNames = orderImages?.map { it.name } ?: emptyList()

                val orderInfo = OrderInfo(
                    id = order.id,
                    userName = user.username,
                    orderName = order.orderName,
                    createdAt = order.createdAt,
                    images = imagesNames
                )

                call.respond(HttpStatusCode.OK, orderInfo)
            }

            post("new") {
                val request = kotlin.runCatching { call.receiveNullable<OrderRequest>() }.getOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                if (request.orderName.isBlank()) return@post call.respond(HttpStatusCode.BadRequest)

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd hh_mm_ss")
                val date = current.format(formatter)

                ordersDao.insertOrder(
                    Order(
                        userId = request.userId,
                        orderName = request.orderName,
                        createdAt = date
                    )
                ) ?: return@post call.respond(HttpStatusCode.Conflict)

                call.respond(HttpStatusCode.OK)
            }

            post("delete/{id}") {
                val orderId = call.parameters["id"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest)
//            val request = kotlin.runCatching { call.receiveNullable<OrderRequest>() }.getOrNull()
//                ?: return@delete call.respond(HttpStatusCode.BadRequest)

//            if (request.orderName.isBlank() || request.id < 0) return@post call.respond(HttpStatusCode.BadRequest)
                val listOfImages = imagesDao.getImagesByOrderId(orderId)
                println("$listOfImages")
                //return@post call.respond(HttpStatusCode.OK)
                if (ordersDao.deleteOrder(orderId)){
                    //val listOfImages = imagesDao.getImagesByOrderId(orderId)
                    listOfImages?.forEach{image ->
                        println("need to delete ${image.uri}")
                        val file = File(image.uri)
                        file.delete()
                    }
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("uploadImage"){
                val multipartData = call.receiveMultipart()
                var imageMetaInfo: ImageMetaInfo? = null

                multipartData.forEachPart { part ->
                    when(part){
                        is PartData.FormItem -> {
                            imageMetaInfo = Json.decodeFromString<ImageMetaInfo>(ImageMetaInfo.serializer(), part.value)
                            println("Got imageMetaInfo: $imageMetaInfo")
                        }
                        is PartData.FileItem -> {
                            val fileBytes = part.streamProvider().readBytes()

                            val fileName = DigestUtils.sha256Hex(fileBytes) + ".jpeg"

                            println("File uploaded (${fileBytes.size} bytes): $fileName")

                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd")
                            val date = current.format(formatter)

                            val path = "/uploads/${imageMetaInfo?.orderName ?: "empty"}"

                            val folder = File(path)
                            if (!folder.exists()){
                                folder.mkdirs()
                            }

                            val file = File(path, fileName)
                            file.writeBytes(fileBytes)

                            val responseImage = imagesDao.insertImage(
                                Image(
                                    orderId = imageMetaInfo?.orderId ?: 0,
                                    name = fileName,
                                    uri = file.absolutePath
                                )
                            ) ?: Image(orderId = 0, name = "1234", uri = "1112")

                            call.respond(HttpStatusCode.OK, responseImage)
                        }
                        else -> {}
                    }
                }
            }

            get("image/{name}"){
                val imageName = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)

                val image = imagesDao.getImageByName(imageName) ?: return@get call.respond(HttpStatusCode.NotFound)
                val file = File(image.uri)
                if (file.exists()){
                    call.respondFile(file)
                }
                else{
                    call.respond(HttpStatusCode.NotFound, "Not exists")
                }

            }

            post("image/delete/{name}"){
                val imageName = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)

                val imageDeleted = imagesDao.deleteImage(imageName)
                if (imageDeleted){
                    call.respond(HttpStatusCode.OK)
                }
                else {
                    call.respond(HttpStatusCode.NotFound, "Not exists")
                }

            }
        }
    }
}