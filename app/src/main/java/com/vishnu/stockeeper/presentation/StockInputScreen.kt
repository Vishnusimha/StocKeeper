package com.vishnu.stockeeper.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.viewmodel.StockViewModel
import java.text.DateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInputScreen(
    stockViewModel: StockViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf<LocalDate?>(null) }
    var purchaseDate by remember { mutableStateOf<LocalDate?>(null) }
    var updatedBy by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var shop by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Stock Item") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 0.dp, top = innerPadding.calculateTopPadding()
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Name input
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = name.isEmpty()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Quantity input
                    TextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        isError = quantity.isEmpty() || quantity.toIntOrNull() == null
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Expiration Date Picker
                    DatePickerField(
                        label = "Expiration Date*",
                        selectedDate = expirationDate,
                        onDateSelected = { expirationDate = it },
                        isError = expirationDate == null
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Purchase Date Picker
                    DatePickerField(
                        label = "Purchase Date (Optional)",
                        selectedDate = purchaseDate,
                        onDateSelected = { purchaseDate = it }
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Updated By input
                    TextField(
                        value = updatedBy,
                        onValueChange = { updatedBy = it },
                        label = { Text("Updated By") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Category input
                    TextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Shop input
                    TextField(
                        value = shop,
                        onValueChange = { shop = it },
                        label = { Text("Shop") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fixed Save Button
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && quantity.toIntOrNull() != null && expirationDate != null) {
                                val stockDto = StockDto(
                                    name = name,
                                    quantity = quantity.toInt(),
                                    expirationDate = expirationDate!!.toEpochDay(),
                                    purchaseDate = purchaseDate?.toEpochDay() ?: LocalDate.now()
                                        .toEpochDay(),
                                    updatedBy = updatedBy,
                                    category = category,
                                    shop = shop
                                )
                                stockViewModel.addStockItem(stockDto)
                                navController.popBackStack()
                                Toast.makeText(context, "Stock item saved", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill all required fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    isError: Boolean = false
) {
    // State for managing the date picker dialog visibility
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }

    // State for text field value
    var textFieldValue by remember {
        mutableStateOf(
            selectedDate?.format(
                DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy"
                )
            ) ?: ""
        )
    }

    // Convert LocalDate to milliseconds for DatePickerState
    val selectedDateMillis =
        selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    val initialSelectedDateMillis =
        selectedDateMillis ?: LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()

    // Create DatePickerState
    val datePickerState = remember {
        DatePickerState(
            locale = CalendarLocale.getDefault(),
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialSelectedDateMillis
        )
    }

    // Show the date picker dialog
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { setShowDatePicker(false) },
            title = { Text(label) },
            text = {
                Column {
                    DatePicker(
                        state = datePickerState,
                        title = { Text("Select Date") },
                        headline = {
                            Text(
                                text = datePickerState.selectedDateMillis?.let { dateMillis ->
                                    DateFormat.getDateInstance().format(Date(dateMillis))
                                } ?: "No date selected",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        },
                        showModeToggle = true,
                        colors = DatePickerDefaults.colors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate =
                                LocalDate.ofEpochDay(millis / 86400000) // Convert millis to LocalDate
                            onDateSelected(newDate)
                            textFieldValue =
                                newDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        }
                        setShowDatePicker(false)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { setShowDatePicker(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DatePickerField UI
    TextField(
        value = textFieldValue,
        onValueChange = { newText ->
            textFieldValue = newText
            try {
                val newDate = LocalDate.parse(newText, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                onDateSelected(newDate)
            } catch (e: Exception) {
                // Handle parsing exception or invalid date format
                // Optionally, you can provide feedback to the user
            }
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        trailingIcon = {
            IconButton(onClick = { setShowDatePicker(true) }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        }
    )
}
