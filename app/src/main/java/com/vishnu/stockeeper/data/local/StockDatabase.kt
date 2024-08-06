package com.vishnu.stockeeper.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StockEntity::class, ItemNameEntity::class, CategoryEntity::class, ShopEntity::class, SelectedStockItem::class, SelectedStockItemList::class],
    version = 1, exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    abstract fun stockItemDao(): StockItemDao
    abstract fun itemNameDao(): ItemNameDao
    abstract fun categoryDao(): CategoryDao
    abstract fun shopDao(): ShopDao
    abstract fun selectedStockItemDao(): SelectedStockItemDao
}
