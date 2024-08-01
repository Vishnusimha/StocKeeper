package com.vishnu.stockeeper.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishnu.stockeeper.data.local.StockEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockEntityCard(stockEntity: StockEntity) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = "${stockEntity.name} : ${stockEntity.quantity}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Expiry Date: ${formatDate(stockEntity.expirationDate)}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Updated by: ${stockEntity.updatedBy}",
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

fun formatDate(dateInMillis: Long): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.format(Date(dateInMillis))
}

@Preview(showBackground = true)
@Composable
fun PreviewStockEntityCard() {
    StockEntityCard(
        stockEntity = StockEntity(
            id = 1,
            name = "Sample Item",
            quantity = 20,
            expirationDate = System.currentTimeMillis() + 86400000 * 10, // 10 days from now
            purchaseDate = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
            updatedBy = "user123"
        )
    )
}
