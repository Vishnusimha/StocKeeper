package com.vishnu.stockeeper.di

import android.content.Context
import androidx.room.Room
import com.vishnu.stockeeper.data.local.StockDatabase
import com.vishnu.stockeeper.repository.StockManager
import com.vishnu.stockeeper.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // STOCK FEATURE
    @Provides
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return Room.databaseBuilder(
            context,
            StockDatabase::class.java,
            "stock_database"
        ).build()
    }

    @Provides
    fun provideStockRepository(stockDatabase: StockDatabase): StockRepository {
        return StockRepository(
            stockItemDao = stockDatabase.stockItemDao(),
            itemNameDao = stockDatabase.itemNameDao(),
            categoryDao = stockDatabase.categoryDao(),
            shopDao = stockDatabase.shopDao(),
            selectedItemDao = stockDatabase.selectedStockItemDao(),
            selectedItemListDao = stockDatabase.selectedItemListDao(),
        )
    }

    @Provides
    fun provideStockManager(
        stockRepository: StockRepository,
    ): StockManager {
        return StockManager(stockRepository)
    }
}
