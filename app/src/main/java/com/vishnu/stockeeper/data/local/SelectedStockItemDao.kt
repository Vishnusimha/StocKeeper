package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SelectedStockItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedStockItemList(list: SelectedStockItemList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedStockItems(items: List<SelectedStockItem>)

    @Update
    suspend fun updateSelectedStockItems(items: List<SelectedStockItem>)

    @Query("DELETE FROM selected_stock_items WHERE listId = :listId")
    suspend fun deleteSelectedStockItemsByListId(listId: String)

    @Query("DELETE FROM selected_stock_item_lists WHERE listId = :listId")
    suspend fun deleteSelectedStockItemList(listId: String)

    @Query("SELECT * FROM selected_stock_items WHERE listId = :listId")
    suspend fun getSelectedStockItemsByListId(listId: String): List<SelectedStockItem>

    @Query("SELECT * FROM selected_stock_item_lists")
    suspend fun getAllSelectedStockItemLists(): List<SelectedStockItemList>

    @Query("SELECT * FROM selected_stock_item_lists WHERE listId = :listId")
    suspend fun getSelectedStockItemListById(listId: String): SelectedStockItemList?
}
