package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** This ProductsDao is used withProductEntity and is used to store all the historic items that were once present in the stock and this data is used to make shopping plan for future*/
@Dao
interface ProductsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(itemName: ProductEntity)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE name = :name LIMIT 1")
    suspend fun getProductName(name: String): ProductEntity?

    @Query("SELECT DISTINCT categoryName FROM products")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT DISTINCT shopName FROM products")
    suspend fun getAllShops(): List<String>

    // Filtering item names by category
    @Query("SELECT name FROM products WHERE categoryName = :categoryName")
    suspend fun getProductsByCategory(categoryName: String): List<String>

    // Filtering item names by shop
    @Query("SELECT name FROM products WHERE shopName = :shopName")
    suspend fun getProductsByShop(shopName: String): List<String>
}
