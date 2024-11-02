package com.example.chooseyouroutfit

import android.app.Application
import com.example.chooseyouroutfit.data.database.AppDatabase
import com.example.chooseyouroutfit.data.repository.ImageRepository
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
                    factory { ImageRepository(get()) }//val IODR by inject<ImageRepository>()
                }
            )
        }
    }
}