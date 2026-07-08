package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.ProductionDatabase
import com.example.data.ProductionRepository
import com.example.data.UserPreferencesRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProductionApp : Application() {
    lateinit var database: ProductionDatabase
    lateinit var productionRepository: ProductionRepository
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            ProductionDatabase::class.java,
            "production_control_db"
        ).fallbackToDestructiveMigration().build()

        productionRepository = ProductionRepository(database)
        userPreferencesRepository = UserPreferencesRepository(this)

        // Prepopulate default users in the background
        MainScope().launch {
            productionRepository.prepopulateDefaultUsers()
        }
    }
}
