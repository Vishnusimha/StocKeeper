package com.vishnu.stockeeper.data

import com.vishnu.stockeeper.data.local.SelectedStockItem
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

fun StockItemSelection.toSelectedStockItem(listId: String): SelectedStockItem {
    return SelectedStockItem(
        id = this.id,
        name = this.name,
        isSelected = this.isSelected,
        quantity = this.quantity,
        listId = listId
    )
}

fun SelectedStockItem.toStockItemSelection(): StockItemSelection {
    return StockItemSelection(
        id = this.id,
        name = this.name,
        isSelected = this.isSelected,
        quantity = this.quantity
    )
}
