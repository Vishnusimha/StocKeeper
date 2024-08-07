package com.vishnu.stockeeper.repository

import android.util.Log
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.local.PreparedPlanEntity
import com.vishnu.stockeeper.data.local.ProductEntity
import com.vishnu.stockeeper.data.local.SelectedProductEntity
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
        return localRepo.getProductById(itemId)
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
        return localRepo.getAllProductsSortedByName()
    }

    suspend fun getAllItemsSortedByExpirationDate(): List<StockEntity> {
        return localRepo.getAllProductsSortedByExpirationDate()
    }

    suspend fun getAllItemsSortedByQuantity(): List<StockEntity> {
        return localRepo.getAllProductsSortedByQuantity()
    }

    suspend fun getAllStockItemNames(): List<String> {
        return localRepo.getAllStockProductNames()
    }

    suspend fun getAllProducts(): List<ProductEntity> {
        return localRepo.getAllProducts()
    }

    suspend fun getAllCategories(): List<String> {
        return localRepo.getAllCategories()
    }

    suspend fun getAllShops(): List<String> {
        return localRepo.getAllShops()
    }

    suspend fun getProductsByCategory(categoryName: String): List<String> {
        return localRepo.getProductsByCategory(categoryName)
    }

    suspend fun getProductsByShop(shopName: String): List<String> {
        return localRepo.getProductsByShop(shopName)
    }

    //    SelectedProductDto
    suspend fun insertSelectedProductList(list: PreparedPlanEntity) {
        localRepo.insertSelectedProductList(list)
    }

    suspend fun insertOrUpdateSelectedProducts(selectedProductEntities: List<SelectedProductEntity>) {
        localRepo.insertOrUpdateSelectedProducts(selectedProductEntities)
    }

    suspend fun deleteSelectedProductsByListId(listId: String) {
        localRepo.deleteSelectedProductsByListId(listId)
    }

    suspend fun deleteSelectedProductList(listId: String) {
        localRepo.deleteSelectedProductList(listId)
    }

    suspend fun getAllPreparedPlanLists(): List<PreparedPlanEntity> {
        return localRepo.getAllSelectedProductLists()
    }

    suspend fun getPreparedPlan(listId: String): List<SelectedProductEntity> {
        return localRepo.getPreparedPlan(listId)
    }
}
