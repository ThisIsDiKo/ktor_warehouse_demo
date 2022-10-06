package com.example.domain.daofacade

import com.example.domain.models.Image

interface ImagesDaoFacade {
    suspend fun getImagesByOrderId(orderId: Int): List<Image>?
    suspend fun getImageById(imageId: Int): Image?
    suspend fun getImageByName(imageName: String): Image?
    suspend fun insertImage(image: Image): Image?
    suspend fun deleteImage(imageName: String): Boolean
}