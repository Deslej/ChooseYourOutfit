package com.example.chooseyouroutfit.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chooseyouroutfit.data.dao.ClothesDao
import com.example.chooseyouroutfit.data.dao.OutfitDao
import com.example.chooseyouroutfit.data.dao.OutfitItemDao
import com.example.chooseyouroutfit.data.entities.Clothes
import com.example.chooseyouroutfit.data.entities.Outfit
import com.example.chooseyouroutfit.data.entities.OutfitItem
import com.example.chooseyouroutfit.model.Converters

@Database(
    entities = [
        Clothes::class,
        Outfit::class,
        OutfitItem::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clothesDao(): ClothesDao
    abstract fun outfitDao(): OutfitDao
    abstract fun outfitItemDao(): OutfitItemDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}