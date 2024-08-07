package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SelectedProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(selectedProductEntities: List<SelectedProductEntity>)

    @Query("SELECT * FROM SelectedProducts WHERE listId = :listId")
    suspend fun getPreparedPlan(listId: String): List<SelectedProductEntity>
}
