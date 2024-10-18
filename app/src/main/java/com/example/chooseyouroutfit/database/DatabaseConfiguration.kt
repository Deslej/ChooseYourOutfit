package com.example.chooseyouroutfit.database

import android.content.Context
import androidx.room.Room

object DatabaseConfiguration {
    fun getDatabase(context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "imageObject-database")
        .build()


}