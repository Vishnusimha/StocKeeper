package com.vishnu.stockeeper.data

import com.vishnu.stockeeper.data.local.StockEntity

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
