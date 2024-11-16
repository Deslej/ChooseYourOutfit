package com.example.chooseyouroutfit.model

import java.io.Serializable

data class ClothesHolder(
    val name: String,
    val category: String,
    val color: String,
    val season: String,
    val material: String,
): Serializable