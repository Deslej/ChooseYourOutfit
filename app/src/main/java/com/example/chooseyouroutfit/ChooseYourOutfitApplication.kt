package com.example.chooseyouroutfit

import android.app.Application
import com.example.chooseyouroutfit.database.DatabaseConfiguration
import com.example.chooseyouroutfit.database.ImageObjectDatabaseRepository
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
                    single { DatabaseConfiguration.getDatabase(androidContext()) }
                    factory { ImageObjectDatabaseRepository(get()) }//val IODR by inject<ImageObjectDatabaseRepository>()
                }
            )
        }
    }
}