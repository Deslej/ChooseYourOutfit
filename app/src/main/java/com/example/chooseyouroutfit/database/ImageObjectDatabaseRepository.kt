package com.example.chooseyouroutfit.database

import com.example.chooseyouroutfit.model.ImageObject

class ImageObjectDatabaseRepository (private val db: AppDatabase){
    suspend fun insertImageObject(imageObject: ImageObject){
        db.imageDao().insert(imageObject)
    }
    suspend fun getAllImageObjects():List<ImageObject>{
        return db.imageDao().getAll()
    }

}