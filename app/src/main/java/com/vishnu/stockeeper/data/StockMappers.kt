package com.vishnu.stockeeper.data

import com.vishnu.stockeeper.data.local.SelectedProductEntity
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

fun SelectedProductDto.toSelectedItem(): SelectedProductEntity {
    return SelectedProductEntity(
        productId = this.productId,
        listId = this.listId,
        productName = this.productName,
        isSelected = this.isSelected,
        quantity = this.quantity,
        shopName = this.shopName,
        categoryName = this.categoryName
    )
}

fun SelectedProductEntity.toSelectedItemDto(): SelectedProductDto {
    return SelectedProductDto(
        productId = this.productId,
        listId = this.listId,
        productName = this.productName,
        isSelected = this.isSelected,
        quantity = this.quantity,
        shopName = this.shopName,
        categoryName = this.categoryName
    )
}
