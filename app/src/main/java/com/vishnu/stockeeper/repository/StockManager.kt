package com.vishnu.stockeeper.repository

import android.util.Log
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.local.CategoryEntity
import com.vishnu.stockeeper.data.local.SelectedItem
import com.vishnu.stockeeper.data.local.SelectedItemList
import com.vishnu.stockeeper.data.local.ShopEntity
import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.toStockEntity
import javax.inject.Inject

class StockManager @Inject constructor(
    private val localRepo: StockRepository,
) {
    private var firebaseRepo: FirebaseStockRepository? = null

    fun initFirebaseStockRepository(userUid: String) {
        Log.d("StockManager", "initFirebaseStockRepository")
        firebaseRepo = FirebaseStockRepository(userUid)
    }

    suspend fun addItem(stockDto: StockDto) {
        localRepo.insert(stockDto.toStockEntity())
        firebaseRepo?.addItem(stockDto)
    }

    suspend fun updateItem(stockDto: StockDto) {
        localRepo.update(stockDto.toStockEntity())
        firebaseRepo?.updateItem(stockDto)
    }

    suspend fun deleteItem(itemId: Int) {
        localRepo.delete(itemId)
        firebaseRepo?.deleteItem(itemId)
    }

    suspend fun getAllItemsFromLocal(): List<StockEntity> {
        return localRepo.getAllItems()
    }

    suspend fun getAllItemsFromRemote(): List<StockDto> {
        return firebaseRepo?.getAllItemsFromFirebase() ?: emptyList()
    }

    suspend fun saveAllItemsIntoLocal(items: List<StockEntity>) {
        localRepo.insertAll(items)
    }

    suspend fun getItemById(itemId: Int): StockEntity? {
        return localRepo.getItemById(itemId)
    }

    suspend fun deleteAllItemsFromLocal() {
        localRepo.deleteAll()
    }

    fun deleteAllItemsFromRemote() {
        firebaseRepo?.deleteAllItems()
    }

    fun observeStockItems(onDataChange: (List<StockDto>) -> Unit) {
        firebaseRepo?.observeStockItems(onDataChange)
    }

    suspend fun getAllItemsSortedByName(): List<StockEntity> {
        return localRepo.getAllItemsSortedByName()
    }

    suspend fun getAllItemsSortedByExpirationDate(): List<StockEntity> {
        return localRepo.getAllItemsSortedByExpirationDate()
    }

    suspend fun getAllItemsSortedByQuantity(): List<StockEntity> {
        return localRepo.getAllItemsSortedByQuantity()
    }

    suspend fun getAllItemNames(): List<String> {
        return localRepo.getAllItemNames()
    }

    suspend fun getAllCategories(): List<CategoryEntity> {
        return localRepo.getAllCategories()
    }

    suspend fun getAllShops(): List<ShopEntity> {
        return localRepo.getAllShops()
    }

    suspend fun getItemNamesByCategory(categoryName: String): List<String> {
        return localRepo.getItemNamesByCategory(categoryName)
    }

    suspend fun getItemNamesByShop(shopName: String): List<String> {
        return localRepo.getItemNamesByShop(shopName)
    }

    //    SelectedItemDto
    suspend fun insertSelectedItemList(list: SelectedItemList) {
        localRepo.insertSelectedItemList(list)
    }

    suspend fun insertOrUpdateSelectedItems(items: List<SelectedItem>) {
        localRepo.insertOrUpdateSelectedItems(items)
    }

    suspend fun deleteSelectedItemsByListId(listId: String) {
        localRepo.deleteSelectedItemsByListId(listId)
    }

    suspend fun deleteSelectedItemList(listId: String) {
        localRepo.deleteSelectedItemList(listId)
    }

    suspend fun getAllSelectedItemLists(): List<SelectedItemList> {
        return localRepo.getAllSelectedItemLists()
    }

    suspend fun getItemsForList(listId: String): List<SelectedItem> {
        return localRepo.getItemsForList(listId)
    }
}
