package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.data.entities.OutfitItem

@Dao
interface OutfitItemDao {

    @Insert
    suspend fun insert(outfitItem: OutfitItem)

    @Query("SELECT * FROM outfit_items WHERE outfitId = :outfitId")
    suspend fun getItemsForOutfit(outfitId: Long): List<OutfitItem>
}