package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.entities.Outfit
import com.example.chooseyouroutfit.data.entities.OutfitItem
import com.example.chooseyouroutfit.data.entities.OutfitWithItems

@Dao
interface OutfitDao {

    @Insert
    suspend fun insertOutfit(outfit: Outfit): Long

    @Insert
    suspend fun insertOutfitItem(outfitItem: OutfitItem)

    @Insert
    suspend fun insertOutfitItems(outfitItems: List<OutfitItem>)

    @Transaction
    @Query("SELECT * FROM outfits WHERE outfitId = :outfitId")
    suspend fun getOutfitWithItems(outfitId: Long): OutfitWithItems

    @Transaction
    @Query("SELECT * FROM outfits")
    suspend fun getAllOutfitsWithItems(): List<OutfitWithItems>

    @Query("SELECT * FROM outfits WHERE name LIKE '%' || :name || '%'")
    suspend fun getOutfitsByName(name: String): List<OutfitWithItems>

    // Dodajemy metodę usuwania outfitu
    @Delete
    suspend fun deleteOutfit(outfit: Outfit)

    // Dodajemy metodę usuwania powiązanych rekordów w OutfitItem
    @Query("DELETE FROM outfits WHERE outfitId = :outfitId")
    suspend fun deleteOutfitItemsByOutfitId(outfitId: Long)
}