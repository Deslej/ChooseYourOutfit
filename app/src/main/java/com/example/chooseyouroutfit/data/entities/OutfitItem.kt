package com.example.chooseyouroutfit.data.entities

import androidx.room.Entity

@Entity(
    tableName = "outfit_items",
    primaryKeys = ["outfitId", "clothesId"]
)
data class OutfitItem(

    val outfitId: Long, // FK
    val clothesId: Long // FK
)