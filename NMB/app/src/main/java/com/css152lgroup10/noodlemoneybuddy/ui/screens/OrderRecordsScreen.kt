package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderWithItems
import com.css152lgroup10.noodlemoneybuddy.ui.components.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import com.css152lgroup10.noodlemoneybuddy.utils.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRecordsScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf<String?>(null) }
    val orders by orderViewModel.orders.collectAsState()
    var repeatOrderItems by remember { mutableStateOf<List<com.css152lgroup10.noodlemoneybuddy.data.models.OrderItem>?>(null) }
    var showRepeatDialog by remember { mutableStateOf(false) }

    // Filter orders based on search query
    val filteredOrders = remember(orders, searchQuery) {
        if (searchQuery.isEmpty()) {
            orders
        } else {
            val query = searchQuery.trim().lowercase()
            orders.filter { orderWithItems ->
                // Match order ID
                orderWithItems.order.id.contains(query, ignoreCase = true) ||
                // Match any item name
                orderWithItems.items.any { it.name.lowercase().contains(query) } ||
                // Match amounts (total, tendered, change)
                runCatching { query.toDouble() }.getOrNull()?.let { numQuery ->
                    val total = orderWithItems.order.totalAmount
                    val tendered = orderWithItems.order.amountTendered
                    val change = orderWithItems.order.changeGiven
                    total == numQuery || tendered == numQuery || change == numQuery
                } == true
            }
        }
    }

    // Error handling
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            delay(3000)
            errorMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Order Records",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar
            MorphingAnimation(visible = true) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Search is performed automatically */ },
                    placeholder = "Search orders...",
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Error message
            errorMessage?.let { message ->
                ErrorState(
                    message = message,
                    onRetry = { errorMessage = null }
                )
            }

            // Loading overlay
            LoadingOverlay(isLoading = isLoading)

            if (filteredOrders.isEmpty()) {
                MorphingAnimation(visible = true) {
                    EmptyState(
                        title = if (searchQuery.isEmpty()) "No Order Records" else "No Results Found",
                        message = if (searchQuery.isEmpty()) {
                            "Start taking orders to see them here!"
                        } else {
                            "No orders match your search query"
                        },
                        icon = {
                            PulseAnimation {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "No orders",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                    )
                }
            } else {
                // Statistics card
                if (searchQuery.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon with colored background
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = "Order Summary",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Stats
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Order Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Total Orders
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Total Orders",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = "${orders.size}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    // Total Revenue
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Total Revenue",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = formatCurrency(orders.sumOf { it.order.totalAmount }),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredOrders, key = { it.order.id }) { orderWithItems ->
                        OrderRecordCard(
                            orderWithItems = orderWithItems,
                            onClick = { navController.navigate("order_detail/${orderWithItems.order.id}") },
                            onDelete = { showDeleteDialog = orderWithItems.order.id },
                            onRepeat = {
                                repeatOrderItems = orderWithItems.items
                                showRepeatDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog != null) {
            ConfirmationDialog(
                title = "Delete Order Record?",
                message = "Are you sure you want to delete this order record? This action cannot be undone.",
                onConfirm = {
                    val order = orders.find { it.order.id == showDeleteDialog }
                    if (order != null) {
                        orderViewModel.deleteOrder(order.order)
                        showSuccessMessage = "Order deleted successfully"
                    }
                    showDeleteDialog = null
                },
                onDismiss = { showDeleteDialog = null }
            )
        }

        // Confirmation dialog for repeat order
        if (showRepeatDialog && repeatOrderItems != null) {
            ConfirmationDialog(
                title = "Repeat Order?",
                message = "Do you want to repeat this order? This will pre-fill the order creation screen with the same items.",
                onConfirm = {
                    navController.navigate("order_list?repeat=1")
                    orderViewModel.setPendingOrderItems(repeatOrderItems!!)
                    showRepeatDialog = false
                },
                onDismiss = { showRepeatDialog = false },
                confirmText = "Yes, Repeat",
                dismissText = "Cancel"
            )
        }
    }
}

@Composable
fun OrderRecordCard(
    orderWithItems: OrderWithItems,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onRepeat: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val orderRecord = orderWithItems.order
    val items = orderWithItems.items
    
    RippleAnimation(isPressed = false) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Order #${orderRecord.id.take(8)}...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                .format(orderRecord.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulseAnimation {
                            Text(
                                formatCurrency(orderRecord.totalAmount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (onDelete != null) {
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete order record")
                            }
                        }
                        if (onRepeat != null) {
                            IconButton(onClick = onRepeat) {
                                Icon(Icons.Filled.Replay, contentDescription = "Repeat order")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Badge for item count
                Badge(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text("${items.size} item(s)")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val itemsToShow = items.take(2)
                itemsToShow.forEach { item ->
                    Text(
                        "• ${item.name} (${item.quantity}x)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (items.size > 2) {
                    Text(
                        "• ... and ${items.size - 2} more items",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
} 