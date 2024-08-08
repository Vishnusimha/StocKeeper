package com.vishnu.stockeeper.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StockEntity::class, ProductEntity::class, SelectedProductEntity::class, PreparedPlanEntity::class],
    version = 1, exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    abstract fun stockItemDao(): StockItemDao
    abstract fun itemNameDao(): ProductsDao
    abstract fun selectedStockItemDao(): SelectedProductDao
    abstract fun selectedItemListDao(): PreparedPlanListDao
}
