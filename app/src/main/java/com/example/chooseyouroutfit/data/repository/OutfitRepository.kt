package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.OutfitDao
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.entities.Outfit
import com.example.chooseyouroutfit.data.entities.OutfitItem

// TODO - zaimplementowac metody do widoku
class OutfitRepository(private val outfitDao: OutfitDao) {

    suspend fun addOutfitWithItems(outfitName: String, clothesList: List<Clothes>) {
        val outfitId = outfitDao.insertOutfit(Outfit(name = outfitName))

        val outfitItems = clothesList.map { clothes ->
            OutfitItem(outfitId = outfitId, clothesId = clothes.clothesId)
        }
        outfitDao.insertOutfitItems(outfitItems)
    }

    suspend fun searchClothesByOutfitName(outfitName: String): List<Clothes> {
        val outfitsWithItems = outfitDao.searchOutfits(outfitName)
        return outfitsWithItems.flatMap { it.clothes }
    }
}