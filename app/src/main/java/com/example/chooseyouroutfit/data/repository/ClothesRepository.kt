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

    suspend fun getClothesById(id: Long): Clothes? {
        return clothesDao.getClothesById(id)
    }
}
