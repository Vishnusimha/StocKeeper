package com.vishnu.stockeeper.data

import com.vishnu.stockeeper.data.local.StockEntity
import java.util.Date

fun StockDto.toStockEntity(): StockEntity {
    return StockEntity(
        id = id,
        name = name,
        quantity = quantity,
        expirationDate = expirationDate,
        purchaseDate = purchaseDate,
        updatedBy = updatedBy
    )
}

fun StockEntity.toStockDto(): StockDto {
    return StockDto(
        id = id,
        name = name,
        quantity = quantity,
        expirationDate = expirationDate,
        purchaseDate = purchaseDate,
        updatedBy = updatedBy
    )
}

fun longToDate(timestamp: Long): Date {
    return Date(timestamp)
}

fun dateToLong(date: Date): Long {
    return date.time
}
