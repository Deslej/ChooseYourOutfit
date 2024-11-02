package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.data.entities.Image

@Dao
interface ImageDao {

    @Insert
    suspend fun insert(image: Image)

    @Query("select * from images")
    suspend fun getAllImages(): List<Image>

}