package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shop: ShopEntity)

    @Query("SELECT * FROM shops")
    suspend fun getAllShops(): List<ShopEntity>

    @Query("SELECT * FROM shops WHERE shopName = :name LIMIT 1")
    suspend fun getShop(name: String): ShopEntity?
}
