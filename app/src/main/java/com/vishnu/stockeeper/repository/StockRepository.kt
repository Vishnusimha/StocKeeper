package com.vishnu.stockeeper.repository

import com.vishnu.stockeeper.data.local.CategoryDao
import com.vishnu.stockeeper.data.local.CategoryEntity
import com.vishnu.stockeeper.data.local.ItemNameDao
import com.vishnu.stockeeper.data.local.ItemNameEntity
import com.vishnu.stockeeper.data.local.SelectedItem
import com.vishnu.stockeeper.data.local.SelectedItemDao
import com.vishnu.stockeeper.data.local.SelectedItemList
import com.vishnu.stockeeper.data.local.SelectedItemListDao
import com.vishnu.stockeeper.data.local.ShopDao
import com.vishnu.stockeeper.data.local.ShopEntity
import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.local.StockItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockItemDao: StockItemDao,
    private val itemNameDao: ItemNameDao,
    private val categoryDao: CategoryDao,
    private val shopDao: ShopDao,
    private val selectedItemDao: SelectedItemDao,
    private val selectedItemListDao: SelectedItemListDao,
) {
    suspend fun insert(stockEntity: StockEntity) {
        withContext(Dispatchers.IO) {
            if (itemNameDao.getItemName(stockEntity.name) == null) {
                itemNameDao.insert(ItemNameEntity(stockEntity.name))
            }
            if (categoryDao.getCategory(stockEntity.category) == null) {
                categoryDao.insert(CategoryEntity(stockEntity.category))
            }
            if (shopDao.getShop(stockEntity.shop) == null) {
                shopDao.insert(ShopEntity(stockEntity.shop))
            }

            // Insert the stock item
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
        for (i in items) {
            if (itemNameDao.getItemName(i.name) == null) {
                itemNameDao.insert(ItemNameEntity(i.name))
            }
            if (categoryDao.getCategory(i.category) == null) {
                categoryDao.insert(CategoryEntity(i.category))
            }
            if (shopDao.getShop(i.shop) == null) {
                shopDao.insert(ShopEntity(i.shop))
            }
        }
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

    suspend fun getAllCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    suspend fun getAllShops(): List<ShopEntity> {
        return shopDao.getAllShops()
    }

    suspend fun getItemNamesByCategory(categoryName: String): List<String> {
        return stockItemDao.getItemNamesByCategory(categoryName)
    }

    suspend fun getItemNamesByShop(shopName: String): List<String> {
        return stockItemDao.getItemNamesByShop(shopName)
    }

    //    selected items
    suspend fun insertSelectedItemList(list: SelectedItemList) {
        selectedItemListDao.insert(list)
    }

    suspend fun insertOrUpdateSelectedItems(items: List<SelectedItem>) {
        selectedItemDao.insertAll(items)
    }

    suspend fun deleteSelectedItemsByListId(listId: String) {
        selectedItemListDao.deleteSelectedItemsByListId(listId)
    }

    suspend fun deleteSelectedItemList(listId: String) {
        selectedItemListDao.deleteSelectedItemList(listId)
    }

    suspend fun getAllSelectedItemLists(): List<SelectedItemList> {
        return selectedItemListDao.getAll()
    }

    suspend fun getItemsForList(listId: String): List<SelectedItem> {
        return selectedItemDao.getItemsForList(listId)
    }
}
