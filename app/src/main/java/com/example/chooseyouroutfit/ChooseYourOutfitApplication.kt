package com.example.chooseyouroutfit

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.chooseyouroutfit.data.database.AppDatabase
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.data.repository.OutfitItemRepository
import com.example.chooseyouroutfit.data.repository.OutfitRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ChooseYourOutfitApplication: Application() {
    override fun onCreate() {
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }
        super.onCreate()
        startKoin {
            androidContext(this@ChooseYourOutfitApplication)
            modules(
                module {
                    single { AppDatabase.getDatabase(androidContext()) }
                    single { get<AppDatabase>().clothesDao() }
                    single { get<AppDatabase>().outfitDao() }
                    single { get<AppDatabase>().outfitItemDao() }
                    factory { ClothesRepository(get()) }
                    factory { OutfitItemRepository(get()) }
                    factory { OutfitRepository(get()) }
                }
            )
        }
    }
}