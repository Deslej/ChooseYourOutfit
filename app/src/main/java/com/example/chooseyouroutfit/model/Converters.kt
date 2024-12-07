package com.example.chooseyouroutfit.model

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun fromListToString(clothesIds: List<Long>): String {
        return clothesIds.joinToString(",")
    }

    @TypeConverter
    fun fromStringToList(data: String): List<Long> {
        return data.split(",").map { it.toLong() }
    }
}