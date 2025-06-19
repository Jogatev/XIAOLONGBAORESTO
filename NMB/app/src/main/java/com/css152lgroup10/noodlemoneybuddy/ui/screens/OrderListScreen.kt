package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.data.models.MenuItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderRecord
import com.css152lgroup10.noodlemoneybuddy.ui.components.OrderItemRow
import com.css152lgroup10.noodlemoneybuddy.utils.MenuItems
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp)
    var showCancelOrderConfirmDialog by remember { mutableStateOf(false) }
    var showItemSelectionDialog by remember { mutableStateOf(false) }
    var selectedMenuItemForQuantity by remember { mutableStateOf<MenuItem?>(null) }
    var showFullScreenQuantityDialog by remember { mutableStateOf(false) }
    var showAmountTenderedDialog by remember { mutableStateOf(false) }
    var amountTenderedInput by remember { mutableStateOf("") }
    var changeAmount by remember { mutableStateOf<Double?>(null) }
    var showPaymentSuccessDialog by remember { mutableStateOf(false) }

    // Local state for the current order being created
    var orderItems by remember { mutableStateOf(listOf<OrderItem>()) }

    val totalCost = remember(orderItems) {
        orderItems.sumOf { it.getTotalPrice() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Create New Order") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (orderItems.isNotEmpty()) {
                            showCancelOrderConfirmDialog = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (orderItems.isNotEmpty() && !showAmountTenderedDialog) {
                FloatingActionButton(
                    onClick = { showItemSelectionDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add item", modifier = Modifier.size(40.dp))
                }
            }
        },
        bottomBar = {
            if (orderItems.isNotEmpty() && !showAmountTenderedDialog) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total: ₱${"%.2f".format(totalCost)}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(
                            onClick = { showAmountTenderedDialog = true },
                            shape = buttonShape,
                            enabled = orderItems.isNotEmpty()
                        ) {
                            Text("Checkout")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (orderItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No items in order.\nTap the '+' button to add items.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = { showItemSelectionDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(120.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                "Add item to order",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(orderItems, key = { it.id }) { item ->
                        OrderItemRow(
                            orderItem = item,
                            onDelete = {
                                orderItems = orderItems.filterNot { it.id == item.id }
                            },
                            showDeleteButton = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Dialogs
        if (showCancelOrderConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showCancelOrderConfirmDialog = false },
                title = { Text("Cancel Order?") },
                text = { Text("Are you sure you want to cancel this order? All items will be removed.") },
                confirmButton = {
                    TextButton(onClick = {
                        orderItems = emptyList()
                        showCancelOrderConfirmDialog = false
                        navController.popBackStack()
                    }) { Text("Yes, Cancel") }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelOrderConfirmDialog = false }) { Text("No") }
                }
            )
        }

        if (showItemSelectionDialog) {
            FullScreenItemSelectionDialog(
                onDismissRequest = { showItemSelectionDialog = false },
                onItemSelected = { selectedMenuItem ->
                    selectedMenuItemForQuantity = selectedMenuItem
                    showItemSelectionDialog = false
                    showFullScreenQuantityDialog = true
                }
            )
        }

        selectedMenuItemForQuantity?.let { menuItem ->
            if (showFullScreenQuantityDialog) {
                FullScreenQuantitySelectionDialog(
                    menuItem = menuItem,
                    onDismissRequest = {
                        showFullScreenQuantityDialog = false
                        selectedMenuItemForQuantity = null
                    },
                    onConfirm = { quantity ->
                        val newItem = OrderItem(
                            orderId = "temp", // Will be replaced on save
                            name = menuItem.name,
                            quantity = quantity,
                            price = menuItem.price
                        )
                        orderItems = orderItems + newItem
                        showFullScreenQuantityDialog = false
                        selectedMenuItemForQuantity = null
                    }
                )
            }
        }

        if (showAmountTenderedDialog) {
            AmountTenderedDialog(
                totalCost = totalCost,
                onDismissRequest = { showAmountTenderedDialog = false },
                onConfirmPayment = { tendered ->
                    val change = tendered - totalCost
                    if (change >= 0) {
                        changeAmount = change
                        showAmountTenderedDialog = false
                        showPaymentSuccessDialog = true

                        val orderId = UUID.randomUUID().toString()
                        val orderRecord = OrderRecord(
                            id = orderId,
                            totalAmount = totalCost,
                            amountTendered = tendered,
                            changeGiven = change,
                            timestamp = Date()
                        )
                        val itemsWithOrderId = orderItems.map { it.copy(orderId = orderId) }
                        orderViewModel.addOrder(orderRecord, itemsWithOrderId)
                        orderItems = emptyList()
                    } else {
                        showAmountTenderedDialog = false
                    }
                },
                amountTenderedInput = amountTenderedInput,
                onAmountTenderedChange = { amountTenderedInput = it }
            )
        }

        if (showPaymentSuccessDialog) {
            PaymentSuccessDialog(
                changeAmount = changeAmount ?: 0.0,
                onDismiss = {
                    showPaymentSuccessDialog = false
                    changeAmount = null
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenItemSelectionDialog(
    onDismissRequest: () -> Unit,
    onItemSelected: (MenuItem) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Select an Item") }
                )
            },
            bottomBar = {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(196.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.titleMedium)
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 0.dp,
                        end = 0.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    )
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                items(MenuItems.availableMenuItems, key = { it.id }) { menuItem ->
                    val itemHeight = 112.dp
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(vertical = 4.dp)
                            .clickable { onItemSelected(menuItem) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(menuItem.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "₱${"%.2f".format(menuItem.price)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenQuantitySelectionDialog(
    menuItem: MenuItem,
    onDismissRequest: () -> Unit,
    onConfirm: (quantity: Int) -> Unit
) {
    var currentQuantity by remember { mutableStateOf(1) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Set Quantity for ${menuItem.name}") }
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp)
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { onConfirm(currentQuantity) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp)
                    ) {
                        Text("Confirm", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            if (currentQuantity > 1) currentQuantity--
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Decrease quantity",
                            modifier = Modifier.fillMaxSize(0.7f),
                            tint = if (currentQuantity > 1) MaterialTheme.colorScheme.primary
                                  else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }

                    Text(
                        text = "$currentQuantity",
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.5f)
                    )

                    IconButton(
                        onClick = { currentQuantity++ },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Increase quantity",
                            modifier = Modifier.fillMaxSize(0.7f),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmountTenderedDialog(
    totalCost: Double,
    onDismissRequest: () -> Unit,
    onConfirmPayment: (Double) -> Unit,
    amountTenderedInput: String,
    onAmountTenderedChange: (String) -> Unit
) {
    var showError by remember { mutableStateOf(false) }
    val amountTendered = amountTenderedInput.toDoubleOrNull()
    val isValidAmount = amountTendered != null && amountTendered >= totalCost

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Enter Amount Tendered") },
        text = {
            Column {
                Text("Total amount due: ₱${"%.2f".format(totalCost)}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountTenderedInput,
                    onValueChange = {
                        onAmountTenderedChange(it)
                        showError = false
                    },
                    label = { Text("Amount Tendered") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        if (amountTendered == null) "Please enter a valid amount."
                        else "Amount must be at least ₱${"%.2f".format(totalCost)}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValidAmount) {
                        onConfirmPayment(amountTendered!!)
                    } else {
                        showError = true
                    }
                },
                enabled = amountTenderedInput.isNotEmpty()
            ) { Text("Confirm Payment") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}

@Composable
fun PaymentSuccessDialog(
    changeAmount: Double,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payment Successful!") },
        text = { Text("Change: ₱${"%.2f".format(changeAmount)}") },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
} 