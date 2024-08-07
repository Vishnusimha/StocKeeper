package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SelectedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(selectedItems: List<SelectedItem>)

    @Query("SELECT * FROM SelectedItems WHERE listId = :listId")
    suspend fun getItemsForList(listId: String): List<SelectedItem>
}
