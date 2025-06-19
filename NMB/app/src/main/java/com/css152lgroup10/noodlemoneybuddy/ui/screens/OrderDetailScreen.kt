package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.data.models.MenuItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderRecord
import com.css152lgroup10.noodlemoneybuddy.ui.components.OrderItemRow
import com.css152lgroup10.noodlemoneybuddy.utils.MenuItems
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderRecord: OrderRecord,
    onUpdateOrder: (OrderRecord) -> Unit,
    onDeleteOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(orderRecord.items.map { it.copy() }) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showItemSelectionDialog by remember { mutableStateOf(false) }
    var selectedMenuItemForQuantity by remember { mutableStateOf<MenuItem?>(null) }
    var showFullScreenQuantityDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf(-1) }
    var showInsufficientTenderedDialog by remember { mutableStateOf(false) }
    var showExceedsTenderedDialog by remember { mutableStateOf(false) }
    var showUpdateTenderedDialog by remember { mutableStateOf(false) }
    var newTenderedAmount by remember { mutableStateOf("") }
    var pendingMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    var pendingQuantity by remember { mutableStateOf<Int?>(null) }
    var pendingEditIndex by remember { mutableStateOf(-1) }
    
    val totalCost = items.sumOf { it.getTotalPrice() }
    val isValidTendered = orderRecord.amountTendered >= totalCost

    fun wouldExceedTendered(additionalCost: Double): Boolean {
        return (totalCost + additionalCost) > orderRecord.amountTendered
    }

    fun handleItemUpdate(menuItem: MenuItem, quantity: Int, editIdx: Int) {
        val itemCost = if (editIdx >= 0) {
            menuItem.price * quantity - items[editIdx].getTotalPrice()
        } else {
            menuItem.price * quantity
        }

        if (wouldExceedTendered(itemCost)) {
            pendingMenuItem = menuItem
            pendingQuantity = quantity
            pendingEditIndex = editIdx
            showExceedsTenderedDialog = true
        } else {
            if (editIdx >= 0) {
                items = items.toMutableList().also {
                    it[editIdx] = it[editIdx].copy(quantity = quantity)
                }
            } else {
                items = items + OrderItem(
                    name = menuItem.name,
                    quantity = quantity,
                    price = menuItem.price
                )
            }
        }
    }

    fun applyPendingUpdate() {
        if (pendingMenuItem != null && pendingQuantity != null) {
            if (pendingEditIndex >= 0) {
                items = items.toMutableList().also {
                    it[pendingEditIndex] = it[pendingEditIndex].copy(quantity = pendingQuantity!!)
                }
            } else {
                items = items + OrderItem(
                    name = pendingMenuItem!!.name,
                    quantity = pendingQuantity!!,
                    price = pendingMenuItem!!.price
                )
            }
        }
        pendingMenuItem = null
        pendingQuantity = null
        pendingEditIndex = -1
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit order")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete order record")
                        }
                    } else {
                        IconButton(onClick = {
                            if (isValidTendered) {
                                val updatedOrder = orderRecord.copy(
                                    items = items,
                                    totalAmount = totalCost,
                                    changeGiven = orderRecord.amountTendered - totalCost
                                )
                                onUpdateOrder(updatedOrder)
                                isEditing = false
                            } else {
                                showInsufficientTenderedDialog = true
                            }
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = "Save changes")
                        }
                        IconButton(onClick = { 
                            isEditing = false
                            items = orderRecord.items.map { it.copy() }
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel edit")
                        }
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Order ID:")
                        Text(orderRecord.id.take(8) + "...")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date:")
                        Text(
                            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                .format(orderRecord.timestamp)
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount:", fontWeight = FontWeight.Bold)
                        Text(
                            "₱${"%.2f".format(totalCost)}", 
                            fontWeight = FontWeight.Bold,
                            color = if (!isValidTendered && isEditing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Amount Tendered:")
                        Text("₱${"%.2f".format(orderRecord.amountTendered)}")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Change Given:")
                        Text(
                            "₱${"%.2f".format(orderRecord.amountTendered - totalCost)}",
                            color = if (!isValidTendered && isEditing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (!isValidTendered && isEditing) {
                        Text(
                            "Warning: Total amount exceeds tendered amount!",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Order Items Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Order Items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (isEditing) {
                            Button(onClick = { showItemSelectionDialog = true }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add item")
                                Text("Add Item", modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    items.forEachIndexed { idx, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isEditing) {
                                IconButton(onClick = {
                                    editIndex = idx
                                    selectedMenuItemForQuantity = MenuItems.availableMenuItems.find { it.name == item.name }
                                    showFullScreenQuantityDialog = true
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit quantity")
                                }
                                IconButton(onClick = {
                                    items = items.toMutableList().also { it.removeAt(idx) }
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Remove item")
                                }
                            }
                            OrderItemRow(
                                orderItem = item,
                                showDeleteButton = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Add item dialog
        if (showItemSelectionDialog) {
            FullScreenItemSelectionDialog(
                onDismissRequest = { showItemSelectionDialog = false },
                onItemSelected = { selectedMenuItem ->
                    selectedMenuItemForQuantity = selectedMenuItem
                    showItemSelectionDialog = false
                    showFullScreenQuantityDialog = true
                    editIndex = -1
                }
            )
        }

        // Edit/add quantity dialog
        selectedMenuItemForQuantity?.let { menuItem ->
            if (showFullScreenQuantityDialog) {
                FullScreenQuantitySelectionDialog(
                    menuItem = menuItem,
                    onDismissRequest = {
                        showFullScreenQuantityDialog = false
                        selectedMenuItemForQuantity = null
                        editIndex = -1
                    },
                    onConfirm = { quantity ->
                        handleItemUpdate(menuItem, quantity, editIndex)
                        showFullScreenQuantityDialog = false
                        selectedMenuItemForQuantity = null
                        editIndex = -1
                    }
                )
            }
        }

        // Exceeds tendered amount warning dialog
        if (showExceedsTenderedDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showExceedsTenderedDialog = false
                    pendingMenuItem = null
                    pendingQuantity = null
                    pendingEditIndex = -1
                },
                title = { Text("Total Exceeds Tendered Amount") },
                text = { 
                    Text(
                        "Adding this item would exceed the tendered amount of ₱${"%.2f".format(orderRecord.amountTendered)}.\n\n" +
                        "Current total: ₱${"%.2f".format(totalCost)}\n" +
                        "After change: ₱${"%.2f".format(totalCost + (pendingMenuItem?.price ?: 0.0) * (pendingQuantity ?: 0))}\n\n" +
                        "Would you like to update the tendered amount or modify items?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            showExceedsTenderedDialog = false
                            newTenderedAmount = (totalCost + (pendingMenuItem?.price ?: 0.0) * (pendingQuantity ?: 0)).toString()
                            showUpdateTenderedDialog = true
                        }
                    ) { 
                        Text("Update Tendered Amount") 
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showExceedsTenderedDialog = false
                            pendingMenuItem = null
                            pendingQuantity = null
                            pendingEditIndex = -1
                        }
                    ) { 
                        Text("Cancel") 
                    }
                }
            )
        }

        // Update tendered amount dialog
        if (showUpdateTenderedDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateTenderedDialog = false },
                title = { Text("Update Tendered Amount") },
                text = {
                    Column {
                        Text("New total will be: ₱${"%.2f".format(totalCost + (pendingMenuItem?.price ?: 0.0) * (pendingQuantity ?: 0))}")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTenderedAmount,
                            onValueChange = { newTenderedAmount = it },
                            label = { Text("New Tendered Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val newTendered = newTenderedAmount.toDoubleOrNull()
                            val newTotal = totalCost + (pendingMenuItem?.price ?: 0.0) * (pendingQuantity ?: 0)
                            if (newTendered != null && newTendered >= newTotal) {
                                val updatedOrder = orderRecord.copy(
                                    amountTendered = newTendered
                                )
                                onUpdateOrder(updatedOrder)
                                applyPendingUpdate()
                                showUpdateTenderedDialog = false
                            }
                        },
                        enabled = newTenderedAmount.toDoubleOrNull()?.let { 
                            it >= (totalCost + (pendingMenuItem?.price ?: 0.0) * (pendingQuantity ?: 0))
                        } ?: false
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showUpdateTenderedDialog = false
                            pendingMenuItem = null
                            pendingQuantity = null
                            pendingEditIndex = -1
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Delete dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Order Record?") },
                text = { Text("Are you sure you want to delete this order record?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteOrder(orderRecord.id)
                        showDeleteDialog = false
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Add insufficient tendered amount dialog
        if (showInsufficientTenderedDialog) {
            AlertDialog(
                onDismissRequest = { showInsufficientTenderedDialog = false },
                title = { Text("Cannot Save Changes") },
                text = { 
                    Text(
                        "The total amount (₱${"%.2f".format(totalCost)}) is greater than " +
                        "the tendered amount (₱${"%.2f".format(orderRecord.amountTendered)}).\n\n" +
                        "Please adjust the items or cancel the edit."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInsufficientTenderedDialog = false }) { 
                        Text("OK") 
                    }
                }
            )
        }
    }
} 