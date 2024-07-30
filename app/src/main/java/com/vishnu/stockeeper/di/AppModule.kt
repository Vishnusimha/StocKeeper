package com.vishnu.stockeeper.di

import android.content.Context
import androidx.room.Room
import com.vishnu.stockeeper.data.local.StockDatabase
import com.vishnu.stockeeper.repository.StockManager
import com.vishnu.stockeeper.repository.FirebaseStockRepository
import com.vishnu.stockeeper.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // STOCK FEATURE
    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return Room.databaseBuilder(
            context,
            StockDatabase::class.java,
            "stock_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStockRepository(stockDatabase: StockDatabase): StockRepository {
        return StockRepository(stockDatabase.stockItemDao())
    }

    @Provides
    @Singleton
    fun provideFirestoreRepository(): FirebaseStockRepository {
        return FirebaseStockRepository()
    }

    @Provides
    @Singleton
    fun provideStockManager(
        stockRepository: StockRepository,
        firebaseStockRepository: FirebaseStockRepository
    ): StockManager {
        return StockManager(stockRepository, firebaseStockRepository)
    }
}
