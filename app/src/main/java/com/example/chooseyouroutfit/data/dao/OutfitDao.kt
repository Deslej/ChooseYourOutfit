package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.data.entities.Outfit

@Dao
interface OutfitDao {

    @Insert
    suspend fun insertOutfit(outfit: Outfit)

    @Query("SELECT * FROM outfits WHERE name LIKE :query ORDER BY name")
    suspend fun searchOutfits(query: String): List<Outfit>

    @Query("SELECT name FROM outfits WHERE name LIKE :outfitName ORDER BY name")
    suspend fun searchOutfitsNames(outfitName: String): List<String>

    @Query("SELECT clothesIds FROM outfits WHERE name LIKE :outfitName")
    suspend fun searchClothesIds(outfitName: String): List<List<Long>>
}