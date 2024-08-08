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

    suspend fun addProduct(stockDto: StockDto) {
        localRepo.insert(stockDto.toStockEntity())
        firebaseRepo?.addProduct(stockDto)
    }

    suspend fun updateProduct(stockDto: StockDto) {
        localRepo.update(stockDto.toStockEntity())
        firebaseRepo?.updateProduct(stockDto)
    }

    suspend fun deleteProduct(productId: Int) {
        localRepo.delete(productId)
        firebaseRepo?.deleteProduct(productId)
    }

    suspend fun getAllStockProductsFromLocal(): List<StockEntity> {
        return localRepo.getAllStockProductsFromLocal()
    }

    suspend fun getAllStockProductsFromRemote(): List<StockDto> {
        return firebaseRepo?.getAllStockProductsFromFirebase() ?: emptyList()
    }

    suspend fun saveAllStockProductsIntoLocal(stockProducts: List<StockEntity>) {
        localRepo.insertAll(stockProducts)
    }

    suspend fun getProductById(productId: Int): StockEntity? {
        return localRepo.getProductById(productId)
    }

    suspend fun deleteAllStockProductsFromLocal() {
        localRepo.deleteAll()
    }

    fun deleteAllStockProductsFromRemote() {
        firebaseRepo?.deleteAllProducts()
    }

    fun observeStockProductsFromRemote(onDataChange: (List<StockDto>) -> Unit) {
        firebaseRepo?.observeStockProducts(onDataChange)
    }

    suspend fun getAllProductsSortedByName(): List<StockEntity> {
        return localRepo.getAllProductsSortedByName()
    }

    suspend fun getAllProductsSortedByExpirationDate(): List<StockEntity> {
        return localRepo.getAllProductsSortedByExpirationDate()
    }

    suspend fun getAllProductsSortedByQuantity(): List<StockEntity> {
        return localRepo.getAllProductsSortedByQuantity()
    }

    suspend fun getAllStockProductNames(): List<String> {
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
