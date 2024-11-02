package com.example.chooseyouroutfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "outfits")
data class Outfit(

    @PrimaryKey(autoGenerate = true)
    val outfitId: Long = 0,
    val name: String
)