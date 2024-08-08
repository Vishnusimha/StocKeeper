package com.vishnu.stockeeper.repository

import com.vishnu.stockeeper.data.local.PreparedPlanEntity
import com.vishnu.stockeeper.data.local.PreparedPlanListDao
import com.vishnu.stockeeper.data.local.ProductEntity
import com.vishnu.stockeeper.data.local.ProductsDao
import com.vishnu.stockeeper.data.local.SelectedProductDao
import com.vishnu.stockeeper.data.local.SelectedProductEntity
import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.local.StockItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockProductDao: StockItemDao,
    private val productsDao: ProductsDao,
    private val selectedProductDao: SelectedProductDao,
    private val preparedPlanListDao: PreparedPlanListDao,
) {
    suspend fun insert(stockEntity: StockEntity) {
        withContext(Dispatchers.IO) {
            if (productsDao.getProductName(stockEntity.name) == null) {
                productsDao.insert(
                    ProductEntity(
                        stockEntity.name,
                        stockEntity.category,
                        stockEntity.shop
                    )
                )
            }

            // Insert the stock item
            stockProductDao.insert(stockEntity)
        }
    }

    suspend fun getAllProducts(): List<ProductEntity> {
        return productsDao.getAllProducts()
    }

    // Update an item in the local database
    suspend fun update(stockEntity: StockEntity) {
        withContext(Dispatchers.IO) {
            stockProductDao.update(stockEntity)
        }
    }

    // Delete an item from the local database
    suspend fun delete(itemId: Int) {
        withContext(Dispatchers.IO) {
            stockProductDao.delete(itemId)
        }
    }

    // Get all items from the local database
    suspend fun getAllStockProductsFromLocal(): List<StockEntity> {
        return withContext(Dispatchers.IO) {
            stockProductDao.getAllItems()
        }
    }

    suspend fun insertAll(items: List<StockEntity>) {
        for (i in items) {
            if (productsDao.getProductName(i.name) == null) {
                productsDao.insert(
                    ProductEntity(
                        i.name,
                        i.category,
                        i.shop
                    )
                )
            }
        }
        stockProductDao.insertAll(items)
    }

    suspend fun deleteAll() {
        stockProductDao.deleteAll()
    }

    // Get an item by ID from the local database
    suspend fun getProductById(itemId: Int): StockEntity? {
        return withContext(Dispatchers.IO) {
            stockProductDao.getProductById(itemId)
        }
    }

    suspend fun getAllProductsSortedByName(): List<StockEntity> {
        return stockProductDao.getAllProductsSortedByName()
    }

    suspend fun getAllProductsSortedByExpirationDate(): List<StockEntity> {
        return stockProductDao.getAllProductsSortedByExpirationDate()
    }

    suspend fun getAllProductsSortedByQuantity(): List<StockEntity> {
        return stockProductDao.getAllProductsSortedByQuantity()
    }

    suspend fun getAllStockProductNames(): List<String> {
        return stockProductDao.getAllStockProductNames()
    }

    suspend fun getAllCategories(): List<String> {
        return productsDao.getAllCategories()
    }

    suspend fun getAllShops(): List<String> {
        return productsDao.getAllShops()
    }

    suspend fun getProductsByCategory(categoryName: String): List<String> {
        return productsDao.getProductsByCategory(categoryName)
    }

    suspend fun getProductsByShop(shopName: String): List<String> {
        return productsDao.getProductsByShop(shopName)
    }

    //    selected items
    suspend fun insertSelectedProductList(list: PreparedPlanEntity) {
        preparedPlanListDao.insert(list)
    }

    suspend fun insertOrUpdateSelectedProducts(items: List<SelectedProductEntity>) {
        selectedProductDao.insertAll(items)
    }

    suspend fun deleteSelectedProductsByListId(listId: String) {
        preparedPlanListDao.deleteSelectedProductsByListId(listId)
    }

    suspend fun deleteSelectedProductList(listId: String) {
        preparedPlanListDao.deleteSelectedProductList(listId)
    }

    suspend fun getAllSelectedProductLists(): List<PreparedPlanEntity> {
        return preparedPlanListDao.getAll()
    }

    suspend fun getPreparedPlan(listId: String): List<SelectedProductEntity> {
        return selectedProductDao.getPreparedPlan(listId)
    }
}
