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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.vishnu.stockeeper.util.Util.staticCurrentUser
import com.vishnu.stockeeper.viewmodel.StockViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInputScreen(
    stockViewModel: StockViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") } // Default quantity is 1
    var expirationDate by remember { mutableStateOf<Long?>(null) }
    var purchaseDate by remember { mutableStateOf<Long?>(null) }
    var updatedBy by remember { mutableStateOf(staticCurrentUser) }
    var category by remember { mutableStateOf("Other") } // Default category is "Other"
    var shop by remember { mutableStateOf("General Store") } // Default shop is "General Store"

    // Error states
    var isNameError by remember { mutableStateOf(false) }
    var isQuantityError by remember { mutableStateOf(false) }
    var isExpirationDateError by remember { mutableStateOf(false) }

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
                    .padding(bottom = 0.dp, top = innerPadding.calculateTopPadding())
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
                        isError = isNameError
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
                        isError = isQuantityError
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Expiration Date Picker
                    DatePickerField(
                        label = "Expiration Date*",
                        selectedDateMillis = expirationDate,
                        onDateSelected = { expirationDate = it },
                        isError = isExpirationDateError
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Purchase Date Picker
                    DatePickerField(
                        label = "Purchase Date",
                        selectedDateMillis = purchaseDate,
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

                    // Save Button
                    Button(
                        onClick = {
                            // Validate fields
                            isNameError = name.isEmpty()
                            isQuantityError = quantity.toIntOrNull() == null || quantity.isEmpty()
                            isExpirationDateError = expirationDate == null

                            if (!isNameError && !isQuantityError && !isExpirationDateError) {
                                val stockDto = StockDto(
                                    name = name,
                                    quantity = quantity.toInt(),
                                    expirationDate = expirationDate ?: LocalDate.now().plusDays(10)
                                        .atStartOfDay(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli(),
                                    purchaseDate = purchaseDate ?: LocalDate.now().toEpochDay(),
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
    selectedDateMillis: Long? = null,
    onDateSelected: (Long?) -> Unit,
    isError: Boolean = false
) {
    // State for managing the date picker dialog visibility
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }

    // Get the current date as the default
    val currentDate = LocalDate.now()
    val currentMillis = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // Use the provided date or fall back to the current date
    val initialDateMillis = selectedDateMillis ?: currentMillis

    // Convert the initial date from millis to a formatted string
    val initialFormattedDate = Instant.ofEpochMilli(initialDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    // State for text field value
    var textFieldValue by remember { mutableStateOf(initialFormattedDate) }

    // Get the current locale
    val locale = LocalContext.current.resources.configuration.locales[0]

    // Create DatePickerState inside the composable context
    val datePickerState = remember {
        DatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            locale = locale
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
                        title = {
                            Text("Select Date", Modifier.padding(16.dp))
                        },
                        headline = {
                            Text(
                                datePickerState.selectedDateMillis?.let { millis ->
                                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                } ?: "No date selected",
                                Modifier.padding(16.dp)
                            )
                        },
                        showModeToggle = true,
                        colors = DatePickerDefaults.colors()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millis)
                        textFieldValue = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    }
                    setShowDatePicker(false)
                }) {
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
            // Filter to keep only digits and ensure a maximum of 8 characters for the date
            val digitsOnly = newText.filter { it.isDigit() }.take(8)

            // Automatically format the input as dd-MM-yyyy
            val formattedText = when (digitsOnly.length) {
                in 1..2 -> digitsOnly // dd
                in 3..4 -> "${digitsOnly.substring(0, 2)}-${digitsOnly.substring(2)}" // dd-MM
                in 5..6 -> "${digitsOnly.substring(0, 2)}-${
                    digitsOnly.substring(
                        2,
                        4
                    )
                }-${digitsOnly.substring(4)}" // dd-MM-yy
                7, 8 -> "${digitsOnly.substring(0, 2)}-${
                    digitsOnly.substring(
                        2,
                        4
                    )
                }-${digitsOnly.substring(4)}" // dd-MM-yyyy
                else -> digitsOnly // Fallback, should never reach here
            }

            textFieldValue = formattedText

            if (formattedText.length == 10) { // Full date entered
                try {
                    val newDate =
                        LocalDate.parse(formattedText, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    val millis =
                        newDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    onDateSelected(millis)
                } catch (e: Exception) {
                    // Handle parsing exception or invalid date format
                    onDateSelected(null)
                }
            } else {
                onDateSelected(null)
            }
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        trailingIcon = {
            IconButton(onClick = { setShowDatePicker(true) }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}


