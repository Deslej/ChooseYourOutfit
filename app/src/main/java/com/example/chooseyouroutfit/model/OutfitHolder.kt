package com.example.chooseyouroutfit.model

import java.io.Serializable

data class OutfitHolder(
    val name: String,
    val clothesIds: List<Long>
) : Serializable
