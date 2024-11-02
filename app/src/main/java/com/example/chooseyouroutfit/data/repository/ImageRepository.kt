package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.ImageDao
import com.example.chooseyouroutfit.data.entities.Image

class ImageRepository(private val imageDao: ImageDao) {
    suspend fun insert(image: Image) {
        imageDao.insert(image)
    }

    suspend fun getAllImages(): List<Image> {
        return imageDao.getAllImages();
    }
}