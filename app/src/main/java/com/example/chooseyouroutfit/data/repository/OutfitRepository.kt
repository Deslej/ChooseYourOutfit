package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.OutfitDao
import com.example.chooseyouroutfit.data.entities.Outfit

class OutfitRepository(private val outfitDao: OutfitDao) {

    suspend fun searchOutfitsByName(outfitName: String): List<Outfit> {
        return outfitDao.searchOutfits(outfitName);
    }

    suspend fun searchOutfitsNames(outfitName: String): List<String> {
        return outfitDao.searchOutfitsNames(outfitName)
    }

    suspend fun searchClothesIds(outfitName: String): List<List<Long>> {
        return outfitDao.searchClothesIds(outfitName)
    }

    suspend fun addOutfit(outfit: Outfit) {
        outfitDao.insertOutfit(outfit)
    }
}