package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.OutfitItemDao
import com.example.chooseyouroutfit.data.entities.OutfitItem

class OutfitItemRepository(private val outfitItemDao: OutfitItemDao) {
    suspend fun insert(outfitItem: OutfitItem) {
        outfitItemDao.insert(outfitItem)
    }

    suspend fun getItemsForOutfit(outfitId: Long): List<OutfitItem> {
        return outfitItemDao.getItemsForOutfit(outfitId)
    }
}