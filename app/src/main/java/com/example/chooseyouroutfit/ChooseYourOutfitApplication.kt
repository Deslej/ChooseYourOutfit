package com.example.chooseyouroutfit

import android.app.Application
import com.example.chooseyouroutfit.data.database.AppDatabase
import com.example.chooseyouroutfit.data.repository.CategoryRepository
import com.example.chooseyouroutfit.data.repository.ClothesRepository
import com.example.chooseyouroutfit.data.repository.OutfitItemRepository
import com.example.chooseyouroutfit.data.repository.OutfitRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ChooseYourOutfitApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ChooseYourOutfitApplication)
            modules(
                module {
                    single { AppDatabase.getDatabase(androidContext()) }
                    single { get<AppDatabase>().categoryDao() }
                    single { get<AppDatabase>().clothesDao() }
                    single { get<AppDatabase>().outfitDao() }
                    single { get<AppDatabase>().outfitItemDao() }
                    factory { CategoryRepository(get()) }
                    factory { ClothesRepository(get()) }
                    factory { OutfitItemRepository(get()) }
                    factory { OutfitRepository(get()) }
                }
            )
        }
    }
}