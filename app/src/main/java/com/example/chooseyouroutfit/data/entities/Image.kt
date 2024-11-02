package com.example.chooseyouroutfit.data.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(

    @PrimaryKey(autoGenerate = true)
    val imageId: Long = 0,
    val uri: Uri?
)