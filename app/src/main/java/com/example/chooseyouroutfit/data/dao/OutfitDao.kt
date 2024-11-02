package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.data.entities.Outfit

@Dao
interface OutfitDao {

    @Insert
    suspend fun insert(outfit: Outfit)

    @Query("SELECT * FROM outfits")
    suspend fun getAllOutfits(): List<Outfit>
}