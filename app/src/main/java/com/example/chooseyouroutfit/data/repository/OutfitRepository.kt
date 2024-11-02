package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.OutfitDao
import com.example.chooseyouroutfit.data.entities.Outfit

class OutfitRepository(private val outfitDao: OutfitDao) {
    suspend fun insert(outfit: Outfit) {
        outfitDao.insert(outfit)
    }

    suspend fun getAllOutfits(): List<Outfit> {
        return outfitDao.getAllOutfits()
    }
}