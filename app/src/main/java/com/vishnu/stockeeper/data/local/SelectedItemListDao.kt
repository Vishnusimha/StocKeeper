package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SelectedItemListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(selectedItemList: SelectedItemList)

    @Query("SELECT * FROM SelectedItemLists")
    suspend fun getAll(): List<SelectedItemList>

    @Query("DELETE FROM SelectedItems WHERE listId = :listId")
    suspend fun deleteSelectedItemsByListId(listId: String)

    @Query("DELETE FROM SelectedItemLists WHERE listId = :listId")
    suspend fun deleteSelectedItemList(listId: String)
}
