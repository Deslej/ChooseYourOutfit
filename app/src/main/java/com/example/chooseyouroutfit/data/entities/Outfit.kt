package com.example.chooseyouroutfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.chooseyouroutfit.model.Converters

@Entity(tableName = "outfits")
data class Outfit(

    @PrimaryKey(autoGenerate = true)
    val outfitId: Long = 0,
    val name: String,
    @TypeConverters(Converters::class)
    val clothesIds: List<Long>
)