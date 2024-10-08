package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StockItemDao {
    @Insert
    suspend fun insert(stockEntity: StockEntity)

    @Update
    suspend fun update(stockEntity: StockEntity)

    @Query("DELETE FROM stock_items WHERE id = :itemId")
    suspend fun delete(itemId: Int)

    @Query("SELECT * FROM stock_items")
    suspend fun getAllItems(): List<StockEntity>

    @Query("SELECT * FROM stock_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): StockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StockEntity>)

    @Query("DELETE FROM stock_items")
    suspend fun deleteAll()

    //Sorting
    // Get all items sorted by name
    @Query("SELECT * FROM stock_items ORDER BY name DESC")
    suspend fun getAllItemsSortedByName(): List<StockEntity>

    // Get all items sorted by expiration date
    @Query("SELECT * FROM stock_items ORDER BY expirationDate DESC")
    suspend fun getAllItemsSortedByExpirationDate(): List<StockEntity>

    // Get all items sorted by quantity
    @Query("SELECT * FROM stock_items ORDER BY quantity DESC")
    suspend fun getAllItemsSortedByQuantity(): List<StockEntity>
}
