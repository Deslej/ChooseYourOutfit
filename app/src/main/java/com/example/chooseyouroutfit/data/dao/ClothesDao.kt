package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.data.entities.Clothes

@Dao
interface ClothesDao {

    @Insert
    suspend fun insert(clothes: Clothes)

    @Query("SELECT * FROM clothes")
    suspend fun getAllClothes(): List<Clothes>

    @Query("SELECT * FROM clothes WHERE clothesId = :id")
    suspend fun getClothesById(id: Long): Clothes?

    // TODO - implement the rest of functions to filter clothes
}