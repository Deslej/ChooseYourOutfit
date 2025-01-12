package com.example.chooseyouroutfit.data.repository

import android.content.Context
import com.example.chooseyouroutfit.data.AppDatabase
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.entities.Outfit
import com.example.chooseyouroutfit.data.entities.OutfitItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClothesRepository(context: Context) {
    private val clothesDao = AppDatabase.getDatabase(context).clothesDao()
    private val outfitDao = AppDatabase.getDatabase(context).outfitDao()

    suspend fun insert(clothes: Clothes) {
        clothesDao.insert(clothes)
    }

    suspend fun getAllClothes(): List<Clothes> {
        return clothesDao.getAllClothes()
    }
    suspend fun deleteClothes(clothes: Clothes) {
        clothesDao.deleteClothes(clothes)
    }


    suspend fun getLastInsertedClothes(): Clothes? {
        return clothesDao.getLastInsertedClothes()
    }

    suspend fun update(clothes: Clothes) {
        clothesDao.update(clothes)
    }

    suspend fun getFilteredClothes(
        name: String = "%",
        color: String = "%",
        material: String = "%",
        season: String = "%",
        category: String = "%"
    ): List<Clothes> {
        return clothesDao.getFilteredClothes(
            name = name,
            color = color,
            material = material,
            season = season,
            category = category
        )
    }
    suspend fun insertClothes(clothes: Clothes) {
        withContext(Dispatchers.IO) {
            clothesDao.insert(clothes)
        }
    }
    suspend fun insertOutfit(outfit: Outfit): Long {
        return outfitDao.insertOutfit(outfit)
    }

    suspend fun insertOutfitItems(outfitItems: List<OutfitItem>) {
        outfitDao.insertOutfitItems(outfitItems)
    }

    suspend fun getClothesIdByUri(uri: String): Long? {
        return clothesDao.getClothesByUri(uri)?.clothesId
    }
}
