package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.data.models.MenuItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderRecord
import com.css152lgroup10.noodlemoneybuddy.ui.components.*
import com.css152lgroup10.noodlemoneybuddy.utils.MenuItems
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    var showCancelOrderConfirmDialog by remember { mutableStateOf(false) }
    var showItemSelectionDialog by remember { mutableStateOf(false) }
    var selectedMenuItemForQuantity by remember { mutableStateOf<MenuItem?>(null) }
    var showFullScreenQuantityDialog by remember { mutableStateOf(false) }
    var showAmountTenderedDialog by remember { mutableStateOf(false) }
    var amountTenderedInput by remember { mutableStateOf("") }
    var changeAmount by remember { mutableStateOf<Double?>(null) }
    var showPaymentSuccessDialog by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Local state for the current order being created
    var orderItems by remember { mutableStateOf(listOf<OrderItem>()) }

    val totalCost = remember(orderItems) {
        orderItems.sumOf { it.getTotalPrice() }
    }

    // Error handling
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            delay(3000)
            errorMessage = null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Create New Order",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) 
                },
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = orderItems.isNotEmpty() && !showAmountTenderedDialog,
                enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                exit = scaleOut(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
            ) {
                FloatingAnimation {
                    EnhancedFAB(
                        onClick = { showItemSelectionDialog = true },
                        icon = { 
                            Icon(
                                Icons.Filled.Add, 
                                contentDescription = "Add item", 
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = orderItems.isNotEmpty() && !showAmountTenderedDialog,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(200)
                )
            ) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            PulseAnimation {
                                Text(
                                    "Total: ₱${"%.2f".format(totalCost)}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${orderItems.size} item${if (orderItems.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        RippleAnimation(isPressed = false) {
                            Button(
                                onClick = { showAmountTenderedDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                enabled = orderItems.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Filled.Payment,
                                    contentDescription = "Checkout",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Checkout")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Error message
            errorMessage?.let { message ->
                ErrorState(
                    message = message,
                    onRetry = { errorMessage = null }
                )
            }

            // Loading overlay
            LoadingOverlay(isLoading = isLoading)

            if (orderItems.isEmpty()) {
                MorphingAnimation(visible = true) {
                    EmptyState(
                        title = "No Items in Order",
                        message = "Start by adding items to create your order",
                        icon = {
                            PulseAnimation {
                                Icon(
                                    Icons.Default.AddShoppingCart,
                                    contentDescription = "Empty order",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        },
                        action = {
                            RippleAnimation(isPressed = false) {
                                Button(
                                    onClick = { showItemSelectionDialog = true },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add item",
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        "Add First Item",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    )
                }
            } else {
                StaggeredListAnimation(
                    visible = true,
                    itemCount = orderItems.size
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            orderItems,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        delayMillis = index * 100
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        delayMillis = index * 100
                                    )
                                )
                            ) {
                                EnhancedOrderItemCard(
                                    orderItem = item,
                                    onDelete = {
                                        orderItems = orderItems.filterNot { it.id == item.id }
                                        showSuccessMessage = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Success message
        if (showSuccessMessage) {
            SuccessMessage(
                message = "Item removed from order",
                onDismiss = { showSuccessMessage = false }
            )
        }

        // Dialogs
        if (showCancelOrderConfirmDialog) {
            ConfirmationDialog(
                title = "Cancel Order?",
                message = "Are you sure you want to cancel this order? All items will be removed.",
                onConfirm = {
                    orderItems = emptyList()
                    navController.popBackStack()
                },
                onDismiss = { showCancelOrderConfirmDialog = false },
                confirmText = "Yes, Cancel",
                dismissText = "No",
                isDestructive = true
            )
        }

        if (showItemSelectionDialog) {
            EnhancedItemSelectionDialog(
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
                EnhancedQuantitySelectionDialog(
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
                        showSuccessMessage = true
                    }
                )
            }
        }

        if (showAmountTenderedDialog) {
            EnhancedAmountTenderedDialog(
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
            EnhancedPaymentSuccessDialog(
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

@Composable
fun EnhancedOrderItemCard(
    orderItem: OrderItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = orderItem.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Qty: ${orderItem.quantity} × ₱${"%.2f".format(orderItem.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Total price
            Text(
                text = "₱${"%.2f".format(orderItem.getTotalPrice())}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Delete button
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedItemSelectionDialog(
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
                    title = { 
                        Text(
                            "Select an Item",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    MenuItems.availableMenuItems,
                    key = { it.id }
                ) { menuItem ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    ) {
                        EnhancedMenuItemCard(
                            menuItem = menuItem,
                            onClick = { onItemSelected(menuItem) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedMenuItemCard(
    menuItem: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = "Food item",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Item details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₱${"%.2f".format(menuItem.price)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Arrow icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedQuantitySelectionDialog(
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
                    title = { 
                        Text(
                            "Set Quantity",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(currentQuantity) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Add to Order")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Item info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = menuItem.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₱${"%.2f".format(menuItem.price)} each",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Quantity selector
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
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (currentQuantity > 1) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = "Decrease quantity",
                            modifier = Modifier.size(32.dp),
                            tint = if (currentQuantity > 1) 
                                MaterialTheme.colorScheme.onPrimary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(32.dp))
                    
                    Text(
                        text = "$currentQuantity",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(32.dp))

                    IconButton(
                        onClick = { currentQuantity++ },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Increase quantity",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Total price
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Price",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "₱${"%.2f".format(menuItem.price * currentQuantity)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedAmountTenderedDialog(
    totalCost: Double,
    onDismissRequest: () -> Unit,
    onConfirmPayment: (Double) -> Unit,
    amountTenderedInput: String,
    onAmountTenderedChange: (String) -> Unit
) {
    var showError by remember { mutableStateOf(false) }
    val amountTendered = amountTenderedInput.toDoubleOrNull()
    val isValidAmount = amountTendered != null && amountTendered >= totalCost
    val change = if (amountTendered != null) amountTendered - totalCost else 0.0

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { 
            Text(
                "Payment Details",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            ) 
        },
        text = {
            Column {
                // Total amount card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Total Amount Due",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "₱${"%.2f".format(totalCost)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Amount tendered input
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAmountTenderedChange("%.2f".format(totalCost)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Exact Amount")
                }
                
                if (showError) {
                    Text(
                        if (amountTendered == null) "Please enter a valid amount."
                        else "Amount must be at least ₱${"%.2f".format(totalCost)}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                // Change preview
                if (amountTendered != null && amountTendered >= totalCost) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Change to Give",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                "₱${"%.2f".format(change)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValidAmount) {
                        onConfirmPayment(amountTendered!!)
                    } else {
                        showError = true
                    }
                },
                enabled = amountTenderedInput.isNotEmpty(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Filled.Payment,
                    contentDescription = "Confirm payment",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun EnhancedPaymentSuccessDialog(
    changeAmount: Double,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Payment Successful!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            ) 
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Order completed successfully!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Change Given",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "₱${"%.2f".format(changeAmount)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
} 