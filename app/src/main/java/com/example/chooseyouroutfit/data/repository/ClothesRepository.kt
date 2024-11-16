package com.example.chooseyouroutfit.data.repository

import com.example.chooseyouroutfit.data.dao.ClothesDao
import com.example.chooseyouroutfit.data.entities.Clothes

class ClothesRepository(private val clothesDao: ClothesDao) {
    suspend fun insert(clothes: Clothes) {
        clothesDao.insert(clothes)
    }

    suspend fun getAllClothes(): List<Clothes> {
        return clothesDao.getAllClothes()
    }

    suspend fun getFilteredClothes(
        color: String = "%",
        material: String = "%",
        season: String = "%",
        category: String = "%"
    ): List<Clothes> {
        return clothesDao.getFilteredClothes(
            color = color,
            material = material,
            season = season,
            category = category
        )
    }
}
