package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemNameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(itemName: ItemNameEntity)

    @Query("SELECT * FROM item_names")
    suspend fun getAllItemNames(): List<ItemNameEntity>

    @Query("SELECT * FROM item_names WHERE name = :name LIMIT 1")
    suspend fun getItemName(name: String): ItemNameEntity?
}
