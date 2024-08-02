package com.vishnu.stockeeper.repository

import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.local.StockItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StockRepository @Inject constructor(private val stockItemDao: StockItemDao) {

    // Insert an item into the local database
    suspend fun insert(stockEntity: StockEntity) {
        withContext(Dispatchers.IO) {
            stockItemDao.insert(stockEntity)
        }
    }

    // Update an item in the local database
    suspend fun update(stockEntity: StockEntity) {
        withContext(Dispatchers.IO) {
            stockItemDao.update(stockEntity)
        }
    }

    // Delete an item from the local database
    suspend fun delete(itemId: Int) {
        withContext(Dispatchers.IO) {
            stockItemDao.delete(itemId)
        }
    }

    // Get all items from the local database
    suspend fun getAllItems(): List<StockEntity> {
        return withContext(Dispatchers.IO) {
            stockItemDao.getAllItems()
        }
    }

    suspend fun insertAll(items: List<StockEntity>) {
        stockItemDao.insertAll(items)
    }

    suspend fun deleteAll() {
        stockItemDao.deleteAll()
    }

    // Get an item by ID from the local database
    suspend fun getItemById(itemId: Int): StockEntity? {
        return withContext(Dispatchers.IO) {
            stockItemDao.getItemById(itemId)
        }
    }

    suspend fun getAllItemsSortedByName(): List<StockEntity> {
        return stockItemDao.getAllItemsSortedByName()
    }

    suspend fun getAllItemsSortedByExpirationDate(): List<StockEntity> {
        return stockItemDao.getAllItemsSortedByExpirationDate()
    }

    suspend fun getAllItemsSortedByQuantity(): List<StockEntity> {
        return stockItemDao.getAllItemsSortedByQuantity()
    }

    suspend fun getAllItemNames(): List<String> {
        return stockItemDao.getAllItemNames()
    }
}
