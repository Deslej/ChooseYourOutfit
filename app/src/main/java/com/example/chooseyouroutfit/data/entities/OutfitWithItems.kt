package com.example.chooseyouroutfit.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class OutfitWithItems(
    @Embedded val outfit: Outfit,
    @Relation(
        parentColumn = "outfitId",
        entityColumn = "clothesId",
        associateBy = Junction(OutfitItem::class)
    )
    val clothes: List<Clothes>
)

