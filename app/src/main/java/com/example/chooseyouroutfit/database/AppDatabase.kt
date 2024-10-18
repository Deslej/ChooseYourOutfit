package com.example.chooseyouroutfit.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chooseyouroutfit.model.Converters
import com.example.chooseyouroutfit.model.ImageObject

@Database(entities =[ImageObject::class] , version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}