package com.example.chooseyouroutfit.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class ImageObject (
    val Uri :Uri?,
    val TypeClothe :String,
    @PrimaryKey val id :String = UUID.randomUUID().toString()
)

enum class TypeClothe(val typeClothe: String){
    SHIRT("Shirt"),
    TROUSERS("Trousers")
}
