package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.OutfitDao
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.entities.Outfit
import com.example.chooseyouroutfit.data.entities.OutfitItem
import com.example.chooseyouroutfit.data.entities.OutfitWithItems

class OutfitRepository(private val outfitDao: OutfitDao) {

    suspend fun getOutfitsByName(name: String): List<OutfitWithItems> {
        return outfitDao.getOutfitsByName(name)
    }

    suspend fun deleteOutfit(outfit: Outfit) {
        // Usuwamy powiązane rekordy w tabeli OutfitItem
        outfitDao.deleteOutfitItemsByOutfitId(outfit.outfitId)
        // Usuwamy outfit z tabeli Outfit
        outfitDao.deleteOutfit(outfit)
    }
    suspend fun getAllOutfits(): List<OutfitWithItems> {
        return outfitDao.getAllOutfitsWithItems()
    }
}