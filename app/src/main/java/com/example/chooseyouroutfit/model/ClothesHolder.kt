package com.example.chooseyouroutfit.model

import java.io.Serializable

data class ClothesHolder(
    val name: String,
    val categoryId: Long,
    val color: String,
    val season: String,
    val material: String,
): Serializable