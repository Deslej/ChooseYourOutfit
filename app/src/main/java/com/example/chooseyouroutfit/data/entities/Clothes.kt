package com.example.chooseyouroutfit.data.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class Clothes(

    @PrimaryKey(autoGenerate = true)
    val clothesId: Long = 0,
    val name: String,
    val categoryId: Long, // FK
    val color: String,
    val season: String,
    val material: String,
    val imageId: Long, //FK
    val uri: Uri
)