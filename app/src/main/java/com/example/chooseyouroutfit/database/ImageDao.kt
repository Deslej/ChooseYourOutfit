package com.example.chooseyouroutfit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.model.ImageObject

@Dao
interface ImageDao {

    @Insert
    suspend fun insert(image: ImageObject)

    @Query("select * from imageobject")
    suspend fun getAll():List<ImageObject>

}