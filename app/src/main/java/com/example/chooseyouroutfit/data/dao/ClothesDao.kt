package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chooseyouroutfit.data.entities.Clothes

@Dao
interface ClothesDao {

    @Insert
    suspend fun insert(clothes: Clothes)

    @Query("SELECT * FROM clothes")
    suspend fun getAllClothes(): List<Clothes>

    @Query("SELECT * FROM clothes WHERE name LIKE :name AND color LIKE :color AND material LIKE :material AND season LIKE :season AND category LIKE :category")
    suspend fun getFilteredClothes(
        name: String = "%",
        color: String = "%",
        material: String = "%",
        season: String = "%",
        category: String = "%"
    ): List<Clothes>
}