package com.example.chooseyouroutfit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Delete
    suspend fun deleteClothes(clothes: Clothes)

    @Query("SELECT * FROM clothes WHERE uri = :uri LIMIT 1")
    suspend fun getClothesByUri(uri: String): Clothes?

    @Update
    suspend fun update(clothes: Clothes)

    @Query("SELECT * FROM clothes ORDER BY clothesId DESC LIMIT 1")
    suspend fun getLastInsertedClothes(): Clothes?
}