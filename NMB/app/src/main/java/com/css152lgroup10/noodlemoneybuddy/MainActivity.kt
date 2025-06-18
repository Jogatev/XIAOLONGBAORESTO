package com.css152lgroup10.noodlemoneybuddy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // includes all layout imports
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.* // includes shape utilities
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // ArrowBack, Download, FileOpen, Add, AddCircle, Delete
import androidx.compose.material3.* // covers all material3 components
import androidx.compose.runtime.* // for remember, mutableStateOf, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.* // includes Calendar, Date, Locale, UUID


// Data class for OrderItem
data class OrderItem(
    val id: String = UUID.randomUUID().toString(), // Default unique ID
    val name: String,
    val quantity: Int,
    val price: Double
) {
    fun getTotalPrice(): Double = quantity * price
}

// Data class for Selectable Menu Items
data class MenuItem(
    val id: String,
    val name: String,
    val price: Double
)

data class OrderStatistics(
    val totalOrders: Int,
    val totalRevenue: Double,
    val averageOrderValue: Double,
    val mostPopularItem: String,
    val mostPopularItemCount: Int,
    val todayOrders: Int,
    val todayRevenue: Double,
    val thisWeekOrders: Int,
    val thisWeekRevenue: Double,
    val thisMonthOrders: Int,
    val thisMonthRevenue: Double
)

// Data class for a completed Order Record
data class OrderRecord(
    val id: String = UUID.randomUUID().toString(), // Unique ID for the order
    val items: List<OrderItem>,
    val totalAmount: Double,
    val amountTendered: Double,
    val changeGiven: Double,
    val timestamp: Date // To store when the order was made
)

// Available menu items
val availableMenuItems = listOf(
    MenuItem("noodle_a", "Spicy Ramen", 250.00),
    MenuItem("noodle_b", "Beef Mami", 180.00),
    MenuItem("drink_c", "Iced Tea", 60.00),
    MenuItem("side_d", "Gyoza (3pcs)", 80.00),
    MenuItem("noodle_e", "Chicken Noodle Soup", 170.00),
    MenuItem("drink_f", "Coke", 50.00),
    MenuItem("side_g", "California Maki (4pcs)", 120.00)

)

// Define route names as constants
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ORDER_LIST_SCREEN = "order_list"
    const val ORDER_RECORDS_SCREEN = "order_records"
    const val ORDER_DETAIL_SCREEN = "order_detail"
    const val STATISTICS_SCREEN = "statistics" // Add this line
}

class ClickDebouncer(private val delayMillis: Long = 500L) {
    private var lastClickTime = 0L

    fun processClick(onClick: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delayMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

@Composable
fun rememberClickDebouncer(delayMillis: Long = 500L): ClickDebouncer {
    return remember { ClickDebouncer(delayMillis) }
}

// Add this function to calculate statistics
fun calculateStatistics(orderRecords: List<OrderRecord>): OrderStatistics {
    if (orderRecords.isEmpty()) {
        return OrderStatistics(0, 0.0, 0.0, "None", 0, 0, 0.0, 0, 0.0, 0, 0.0)
    }

    val totalOrders = orderRecords.size
    val totalRevenue = orderRecords.sumOf { it.totalAmount }
    val averageOrderValue = totalRevenue / totalOrders

    // Find most popular item
    val itemCounts = mutableMapOf<String, Int>()
    orderRecords.forEach { order ->
        order.items.forEach { item ->
            itemCounts[item.name] = itemCounts.getOrDefault(item.name, 0) + item.quantity
        }
    }
    val mostPopularItem = itemCounts.maxByOrNull { it.value }
    val mostPopularItemName = mostPopularItem?.key ?: "None"
    val mostPopularItemCount = mostPopularItem?.value ?: 0

    // Calculate time-based statistics
    val calendar = Calendar.getInstance()
    val today = calendar.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfToday = calendar.time

    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val startOfWeek = calendar.time

    calendar.time = today
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfMonth = calendar.time

    val todayOrders = orderRecords.filter { it.timestamp >= startOfToday }
    val thisWeekOrders = orderRecords.filter { it.timestamp >= startOfWeek }
    val thisMonthOrders = orderRecords.filter { it.timestamp >= startOfMonth }

    return OrderStatistics(
        totalOrders = totalOrders,
        totalRevenue = totalRevenue,
        averageOrderValue = averageOrderValue,
        mostPopularItem = mostPopularItemName,
        mostPopularItemCount = mostPopularItemCount,
        todayOrders = todayOrders.size,
        todayRevenue = todayOrders.sumOf { it.totalAmount },
        thisWeekOrders = thisWeekOrders.size,
        thisWeekRevenue = thisWeekOrders.sumOf { it.totalAmount },
        thisMonthOrders = thisMonthOrders.size,
        thisMonthRevenue = thisMonthOrders.sumOf { it.totalAmount }
    )
}


// Export functions
fun exportToCSV(context: Context, orderRecords: List<OrderRecord>): Boolean {
    return try {
        val fileName = "noodle_buddy_orders_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            // Write header
            writer.append("Order ID,Date,Item Name,Quantity,Unit Price,Total Price,Order Total,Amount Tendered,Change Given\n")

            // Write data
            orderRecords.forEach { order ->
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.timestamp)
                order.items.forEach { item ->
                    writer.append("${order.id},")
                    writer.append("$dateString,")
                    writer.append("\"${item.name}\",")
                    writer.append("${item.quantity},")
                    writer.append("${item.price},")
                    writer.append("${item.getTotalPrice()},")
                    writer.append("${order.totalAmount},")
                    writer.append("${order.amountTendered},")
                    writer.append("${order.changeGiven}\n")
                }
            }
        }

        // Share the file
        shareFile(context, file, "text/csv")
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun exportToExcel(context: Context, orderRecords: List<OrderRecord>): Boolean {
    return try {
        val fileName = "noodle_buddy_orders_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            // Write header with tab separation for Excel compatibility
            writer.append("Order ID\tDate\tItem Name\tQuantity\tUnit Price\tTotal Price\tOrder Total\tAmount Tendered\tChange Given\n")

            // Write data with tab separation
            orderRecords.forEach { order ->
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.timestamp)
                order.items.forEach { item ->
                    writer.append("${order.id}\t")
                    writer.append("$dateString\t")
                    writer.append("${item.name}\t")
                    writer.append("${item.quantity}\t")
                    writer.append("${item.price}\t")
                    writer.append("${item.getTotalPrice()}\t")
                    writer.append("${order.totalAmount}\t")
                    writer.append("${order.amountTendered}\t")
                    writer.append("${order.changeGiven}\n")
                }
            }
        }

        // Share the file as Excel-compatible
        shareFile(context, file, "application/vnd.ms-excel")
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun shareFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share Export File"))
}






class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoodleMoneyBuddyTheme {
                val navController = rememberNavController()
                // State for current order items being built
                // In a real app, this would ideally be in a ViewModel
                val currentOrderItems = remember { mutableStateOf(listOf<OrderItem>()) }
                // State for all saved order records
                // In a real app, this would also be in a ViewModel, possibly fetched from a database
                val orderRecords = remember { mutableStateOf(listOf<OrderRecord>()) }


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        currentOrderItems = currentOrderItems,
                        orderRecords = orderRecords
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentOrderItems: MutableState<List<OrderItem>>,
    orderRecords: MutableState<List<OrderRecord>>
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.MENU_SCREEN,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 150)
            ) + fadeIn(animationSpec = tween(durationMillis = 150))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 150)
            ) + fadeOut(animationSpec = tween(durationMillis = 150))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 150)
            ) + fadeIn(animationSpec = tween(durationMillis = 150))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 150)
            ) + fadeOut(animationSpec = tween(durationMillis = 150))
        }
    ) {
        composable(AppDestinations.MENU_SCREEN) {
            MenuScreen(navController = navController)
        }
        composable(AppDestinations.ORDER_LIST_SCREEN) {
            OrderListScreen(
                navController = navController,
                orderItems = currentOrderItems,
                onSaveOrder = { record ->
                    orderRecords.value = orderRecords.value + record
                    currentOrderItems.value = emptyList()
                }
            )
        }
        composable(AppDestinations.ORDER_RECORDS_SCREEN) {
            OrderRecordsScreen(
                navController = navController,
                orderRecords = orderRecords.value,
                onOrderClick = { orderId ->
                    navController.navigate("${AppDestinations.ORDER_DETAIL_SCREEN}/$orderId")
                }
            )
        }
        composable("${AppDestinations.ORDER_DETAIL_SCREEN}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            val record = orderRecords.value.find { it.id == orderId }
            if (record != null) {
                OrderDetailScreen(navController = navController, orderRecord = record)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Order not found. Please go back.", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                }
            }
        }
        // Add this new composable for statistics
        composable(AppDestinations.STATISTICS_SCREEN) {
            StatisticsScreen(
                navController = navController,
                orderRecords = orderRecords.value
            )
        }
    }
}
@Composable
fun MenuScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val lessRoundedButtonShape = RoundedCornerShape(8.dp)
    val debouncer = rememberClickDebouncer()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                debouncer.processClick {
                    navController.navigate(AppDestinations.ORDER_LIST_SCREEN) {
                        launchSingleTop = true
                    }
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) { Text("Create Order") }

        Button(
            onClick = {
                debouncer.processClick {
                    navController.navigate(AppDestinations.ORDER_RECORDS_SCREEN)
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) { Text("Modify Order") }

        Button(
            onClick = {
                debouncer.processClick {
                    navController.navigate(AppDestinations.STATISTICS_SCREEN) // Update this line
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(vertical = 8.dp)
        ) { Text("View Statistics") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    orderItems: MutableState<List<OrderItem>>, // Receive as MutableState to modify
    onSaveOrder: (OrderRecord) -> Unit, // Callback to save the order
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

    val totalCost = remember(orderItems.value) { // Recalculate only when orderItems changes
        orderItems.value.sumOf { it.getTotalPrice() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Create New Order") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (orderItems.value.isNotEmpty()) {
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

        },
        bottomBar = {
            if (orderItems.value.isNotEmpty() && !showAmountTenderedDialog) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Give it a slight distinction
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
                            enabled = orderItems.value.isNotEmpty() // Should always be true if this bar is visible
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
                .padding(innerPadding) // Apply padding from Scaffold
                .fillMaxSize()
        ) {
            if (orderItems.value.isEmpty()) {
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
                            shape = RoundedCornerShape(16.dp), // Square-ish shape
                            modifier = Modifier
                                .size(120.dp) // Large square button
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                "Add item to order",
                                modifier = Modifier.size(48.dp) // Large icon
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Takes up remaining space
                        .padding(horizontal = 16.dp), // Side padding for the list
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp) // Padding for first/last item
                ) {
                    items(orderItems.value, key = { it.id }) { item ->
                        OrderItemRow(
                            orderItem = item,
                            onDelete = {
                                orderItems.value = orderItems.value.filterNot { it.id == item.id }
                            },
                            showDeleteButton = true // Allow deleting items in current order
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Spacing between items
                    }
                }
            }
        }

        // --- Dialogs ---
        if (showCancelOrderConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showCancelOrderConfirmDialog = false },
                title = { Text("Cancel Order?") },
                text = { Text("Are you sure you want to cancel this order? All items will be removed.") },
                confirmButton = {
                    TextButton(onClick = {
                        orderItems.value = emptyList() // Clear items
                        showCancelOrderConfirmDialog = false
                        navController.popBackStack() // Go back
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
                    showItemSelectionDialog = false // Close item selection
                    showFullScreenQuantityDialog = true // Open quantity selection
                }
            )
        }

        selectedMenuItemForQuantity?.let { menuItem ->
            if (showFullScreenQuantityDialog) {
                FullScreenQuantitySelectionDialog(
                    menuItem = menuItem,
                    onDismissRequest = {
                        showFullScreenQuantityDialog = false
                        selectedMenuItemForQuantity = null // Reset
                    },
                    onConfirm = { quantity ->
                        val newItem = OrderItem(
                            // id = UUID.randomUUID().toString(), // id is now defaulted in data class
                            name = menuItem.name,
                            quantity = quantity,
                            price = menuItem.price
                        )
                        orderItems.value = orderItems.value + newItem // Add to the list
                        showFullScreenQuantityDialog = false
                        selectedMenuItemForQuantity = null // Reset
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
                    if (change >= 0) { // Ensure change is not negative
                        changeAmount = change
                        showAmountTenderedDialog = false
                        showPaymentSuccessDialog = true

                        // Create and save the order record
                        val newRecord = OrderRecord(
                            // id = UUID.randomUUID().toString(), // id is now defaulted
                            items = orderItems.value,
                            totalAmount = totalCost,
                            amountTendered = tendered,
                            changeGiven = change,
                            timestamp = Date() // Current timestamp
                        )
                        onSaveOrder(newRecord) // Call the callback to save

                    } else {
                        // Handle insufficient payment (e.g., show a toast or error message)
                        // For now, just closes the dialog, but a Snackbar would be better
                        showAmountTenderedDialog = false
                        // You might want to add a Snackbar here to inform the user:
                        // scope.launch { snackbarHostState.showSnackbar("Amount tendered is less than total cost.") }
                    }
                },
                amountTenderedInput = amountTenderedInput,
                onAmountTenderedChange = { amountTenderedInput = it }
            )
        }

        if (showPaymentSuccessDialog) {
            PaymentSuccessDialog(
                changeAmount = changeAmount ?: 0.0, // Handle null case for change
                onDismiss = {
                    showPaymentSuccessDialog = false
                    changeAmount = null // Reset for next time
                    // orderItems.value is already cleared via onSaveOrder in OrderListScreen
                    navController.popBackStack(AppDestinations.MENU_SCREEN, inclusive = false) // Go back to menu
                }
            )
        }
    }
}


@Composable
fun OrderItemRow(
    orderItem: OrderItem,
    showDeleteButton: Boolean,
    onDelete: (() -> Unit)? = null, // Make onDelete nullable if not always needed
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(orderItem.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Qty: ${orderItem.quantity} @ ₱${"%.2f".format(orderItem.price)} each",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                "₱${"%.2f".format(orderItem.getTotalPrice())}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp) // Give some space before delete button
            )
            if (showDeleteButton && onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete item")
                }
            }
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
            usePlatformDefaultWidth = false, // Full screen
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Select an Item") }
                    // No navigation icon needed as back press / outside click handles dismiss
                )
            },
            bottomBar = {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(196.dp) // Increased height
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Standard padding
                ) {
                    Text("Cancel", style = MaterialTheme.typography.titleMedium)
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 0.dp, // Use contentPadding for side padding
                        end = 0.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    )
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                items(availableMenuItems, key = {it.id}) { menuItem ->
                    val itemHeight = 112.dp // Increased item height
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight) // Apply fixed height
                            .padding(vertical = 4.dp) // Spacing between cards
                            .clickable {
                                onItemSelected(menuItem)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp) // Padding inside the card
                                .fillMaxWidth()
                                .fillMaxHeight(), // Ensure row takes full card height
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
                    Spacer(modifier = Modifier.height(8.dp)) // Spacing after card (optional if padding(vertical=4.dp) on card is enough)
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
            dismissOnClickOutside = false, // Prevent accidental dismiss
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
                        .padding(horizontal = 16.dp, vertical = 12.dp), // Standard padding
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp) // Increased height
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Spacing between buttons
                    Button(
                        onClick = { onConfirm(currentQuantity) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(96.dp) // Increased height
                    ) {
                        Text("Confirm", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding) // Apply inner padding from Scaffold
                    .fillMaxSize()
                    .padding(16.dp), // Additional padding for content
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Quantity Selector UI
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
                            .aspectRatio(1f) // Makes it square-ish
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete, // Using Delete as a minus/decrease icon
                            contentDescription = "Decrease quantity",
                            modifier = Modifier.fillMaxSize(0.7f), // Icon size relative to button
                            tint = if (currentQuantity > 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Disabled look
                        )
                    }

                    Text(
                        text = "$currentQuantity",
                        style = MaterialTheme.typography.displayLarge, // Larger text for quantity
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.5f) // Give quantity text more space
                    )

                    IconButton(
                        onClick = { currentQuantity++ },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f) // Makes it square-ish
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Increase quantity",
                            modifier = Modifier.fillMaxSize(0.7f), // Icon size relative to button
                            tint = MaterialTheme.colorScheme.primary // Always enabled look for add
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
    // val amountTendered = amountTenderedInput.toDoubleOrNull() // Not used directly here, but good for validation

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
                        showError = false // Reset error on change
                    },
                    label = { Text("Amount Tendered") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        "Please enter a valid amount greater than or equal to the total cost.",
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
                    val tendered = amountTenderedInput.toDoubleOrNull()
                    if (tendered != null && tendered >= totalCost) {
                        onConfirmPayment(tendered)
                    } else {
                        showError = true
                    }
                }
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
        onDismissRequest = onDismiss, // Typically you want to control dismissal
        title = { Text("Payment Successful!") },
        text = { Text("Change: ₱${"%.2f".format(changeAmount)}") },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    orderRecords: List<OrderRecord>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statistics = remember(orderRecords) { calculateStatistics(orderRecords) }
    var showExportMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showExportMenu = true }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export Data")
                    }
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export to CSV") },
                            onClick = {
                                showExportMenu = false
                                val success = exportToCSV(context, orderRecords)
                                Toast.makeText(
                                    context,
                                    if (success) "CSV exported successfully!" else "Export failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Face, contentDescription = null)  // Changed to Description
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export to Excel") },
                            onClick = {
                                showExportMenu = false
                                val success = exportToExcel(context, orderRecords)
                                Toast.makeText(
                                    context,
                                    if (success) "Excel file exported successfully!" else "Export failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Face, contentDescription = null)  // Changed to Description
                            }
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Overall Statistics Card
            StatisticsCard(
                title = "Overall Performance",
                color = MaterialTheme.colorScheme.primary
            ) {
                StatisticRow("Total Orders", statistics.totalOrders.toString())
                StatisticRow("Total Revenue", "₱${"%.2f".format(statistics.totalRevenue)}")
                StatisticRow("Average Order Value", "₱${"%.2f".format(statistics.averageOrderValue)}")
                StatisticRow("Most Popular Item", "${statistics.mostPopularItem} (${statistics.mostPopularItemCount} sold)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Statistics
            StatisticsCard(
                title = "Today's Performance",
                color = MaterialTheme.colorScheme.secondary
            ) {
                StatisticRow("Orders Today", statistics.todayOrders.toString())
                StatisticRow("Revenue Today", "₱${"%.2f".format(statistics.todayRevenue)}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // This Week's Statistics
            StatisticsCard(
                title = "This Week's Performance",
                color = MaterialTheme.colorScheme.tertiary
            ) {
                StatisticRow("Orders This Week", statistics.thisWeekOrders.toString())
                StatisticRow("Revenue This Week", "₱${"%.2f".format(statistics.thisWeekRevenue)}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // This Month's Statistics
            StatisticsCard(
                title = "This Month's Performance",
                color = MaterialTheme.colorScheme.error
            ) {
                StatisticRow("Orders This Month", statistics.thisMonthOrders.toString())
                StatisticRow("Revenue This Month", "₱${"%.2f".format(statistics.thisMonthRevenue)}")
            }

            if (orderRecords.isEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No orders found. Start taking orders to see statistics!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    title: String,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 24.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
    Divider(
        modifier = Modifier.padding(vertical = 2.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

// Add these composable functions to your MainActivity.kt file

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRecordsScreen(
    navController: NavController,
    orderRecords: List<OrderRecord>,
    onOrderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Records") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (orderRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No order records found.\nStart taking orders to see them here!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(orderRecords, key = { it.id }) { record ->
                    OrderRecordCard(
                        orderRecord = record,
                        onClick = { onOrderClick(record.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun OrderRecordCard(
    orderRecord: OrderRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    "₱${"%.2f".format(orderRecord.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "${orderRecord.items.size} item(s)",
                style = MaterialTheme.typography.bodyMedium
            )

            // Show first few items
            val itemsToShow = orderRecord.items.take(2)
            itemsToShow.forEach { item ->
                Text(
                    "• ${item.name} (${item.quantity}x)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (orderRecord.items.size > 2) {
                Text(
                    "• ... and ${orderRecord.items.size - 2} more items",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderRecord: OrderRecord,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                        Text("₱${"%.2f".format(orderRecord.totalAmount)}", fontWeight = FontWeight.Bold)
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
                        Text("₱${"%.2f".format(orderRecord.changeGiven)}")
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
                    Text(
                        "Order Items",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    orderRecord.items.forEach { item ->
                        OrderItemRow(
                            orderItem = item,
                            showDeleteButton = false,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}