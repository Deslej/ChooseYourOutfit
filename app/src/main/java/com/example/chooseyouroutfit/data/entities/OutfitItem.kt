package com.example.chooseyouroutfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfit_items")
data class OutfitItem(
    @PrimaryKey(autoGenerate = true)
    val outfitItemId: Long = 0,
    val outfitId: Long,
    val clothesId: Long
)