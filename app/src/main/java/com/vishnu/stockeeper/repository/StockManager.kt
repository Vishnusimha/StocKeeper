package com.vishnu.stockeeper.repository

import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.toStockEntity
import javax.inject.Inject

class StockManager @Inject constructor(
    private val localRepo: StockRepository,
    private val firebaseRepo: FirebaseStockRepository
) {
    suspend fun addItem(stockDto: StockDto) {
        localRepo.insert(stockDto.toStockEntity())
        firebaseRepo.addItem(stockDto)
    }

    suspend fun  updateItem(stockDto: StockDto) {
        localRepo.update(stockDto.toStockEntity())
        firebaseRepo.updateItem(stockDto)
    }

    suspend fun deleteItem(itemId: Int) {
        localRepo.delete(itemId)
        firebaseRepo.deleteItem(itemId)
    }

    // Fetch all items from the local database
    suspend fun getAllItemsFromLocal(): List<StockEntity> {
        return localRepo.getAllItems()
    }

    suspend fun getAllItemsFromRemote(): List<StockDto> {
        return firebaseRepo.getAllItemsFromFirebase()
    }


    // Save a list of items to the local database
    suspend fun saveAllItemsIntoLocal(items: List<StockEntity>) {
        localRepo.insertAll(items)
    }

    // Fetch a specific item by ID from the local database
    suspend fun getItemById(itemId: Int): StockEntity? {
        return localRepo.getItemById(itemId)
    }

    suspend fun deleteAllItemsFromLocal() {
        localRepo.deleteAll()
    }

    fun deleteAllItemsFromRemote() {
        firebaseRepo.deleteAllItems()
    }

    fun observeStockItems(onDataChange: (List<StockDto>) -> Unit) {
        firebaseRepo.observeStockItems(onDataChange)
    }
}
