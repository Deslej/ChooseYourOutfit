package com.example.chooseyouroutfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(

    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val name: String
)