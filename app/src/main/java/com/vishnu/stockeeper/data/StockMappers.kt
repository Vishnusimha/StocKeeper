package com.vishnu.stockeeper.data

import com.vishnu.stockeeper.data.local.SelectedItem
import com.vishnu.stockeeper.data.local.StockEntity
import java.util.Date

fun StockDto.toStockEntity(): StockEntity {
    return StockEntity(
        id = id,
        name = name,
        quantity = quantity,
        expirationDate = expirationDate,
        purchaseDate = purchaseDate,
        updatedBy = updatedBy,
        category = category,
        shop = shop
    )
}

fun StockEntity.toStockDto(): StockDto {
    return StockDto(
        id = id,
        name = name,
        quantity = quantity,
        expirationDate = expirationDate,
        purchaseDate = purchaseDate,
        updatedBy = updatedBy,
        category = category,
        shop = shop
    )
}

fun longToDate(timestamp: Long): Date {
    return Date(timestamp)
}

fun dateToLong(date: Date): Long {
    return date.time
}

fun SelectedItemDto.toSelectedItem(): SelectedItem {
    return SelectedItem(
        itemId = this.itemId,
        listId = this.listId,
        itemName = this.itemName,
        isSelected = this.isSelected,
        quantity = this.quantity
    )
}

fun SelectedItem.toSelectedItemDto(): SelectedItemDto {
    return SelectedItemDto(
        itemId = this.itemId,
        listId = this.listId,
        itemName = this.itemName,
        isSelected = this.isSelected,
        quantity = this.quantity
    )
}
