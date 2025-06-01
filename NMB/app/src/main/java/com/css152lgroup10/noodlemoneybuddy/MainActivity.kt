package com.css152lgroup10.noodlemoneybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme

// Data class for OrderItem
data class OrderItem(
    val id: String,
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

// Available menu items
val availableMenuItems = listOf(
    MenuItem("noodle_a", "Noodle A", 5.00),
    MenuItem("noodle_b", "Noodle B", 6.00)
)

// Define route names as constants
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ORDER_LIST_SCREEN = "order_list"
    // ITEM_SELECTION_SCREEN is no longer needed as a separate route
}

/* During the screen animation, the buttons can still be clicked. So if the user spam taps the button,
 you end up opening more than 1 instance of the screen, which we don't want that. This function temporarily
 disables the button for a certain duration, typically as long as the animation takes.*/
class ClickDebouncer(private val delayMillis: Long = 500L) { // Default 500ms debounce
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoodleMoneyBuddyTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppDestinations.MENU_SCREEN,
                        modifier = Modifier.padding(innerPadding),
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
                            OrderListScreen(navController = navController) // Simplified call
                        }
                        // No composable for ITEM_SELECTION_SCREEN needed here
                    }
                }
            }
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
            onClick = { /* TODO: Handle Modify Order click */ },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) { Text("Modify Order") }
        Button(
            onClick = { /* TODO: Handle View Statistics click */ },
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
fun FullScreenItemSelectionDialog(
    onDismissRequest: () -> Unit,
    onItemSelected: (MenuItem) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true, // Or false, as per your preference
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
                // The Row is no longer strictly necessary if the Button fills the width,
                // but it can be kept for structure or if you plan to add other elements later.
                // For a single full-width button, you can apply modifiers directly to the Button.
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(8.dp), // You can adjust or remove the shape if desired
                    modifier = Modifier
                        .fillMaxWidth() // Make the button span the full width
                        .height(96.dp)  // Set a double height (e.g., 2 * 48.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Apply padding around the button
                ) {
                    Text("Cancel", style = MaterialTheme.typography.titleMedium) // Optional: Larger text
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
                items(availableMenuItems) { menuItem ->
                    val itemHeight = 112.dp
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(vertical = 4.dp)
                            .clickable {
                                onItemSelected(menuItem)
                                onDismissRequest()
                            },
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
                                "$${"%.2f".format(menuItem.price)}",
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


@Composable
fun OrderListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp)
    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    var showItemSelectionDialog by remember { mutableStateOf(false) } // State for the dialog

    val orderItems = remember {
        mutableStateOf(listOf<OrderItem>())
    }

    // --- Full-Screen Item Selection Dialog ---
    if (showItemSelectionDialog) {
        FullScreenItemSelectionDialog(
            onDismissRequest = { showItemSelectionDialog = false },
            onItemSelected = { selectedMenuItem ->
                val newItemId = (orderItems.value.size + 1 + System.currentTimeMillis()).toString()
                orderItems.value = orderItems.value + OrderItem(
                    id = newItemId,
                    name = selectedMenuItem.name,
                    quantity = 1, // Default quantity
                    price = selectedMenuItem.price
                )
                // Dialog is dismissed by onDismissRequest within FullScreenItemSelectionDialog
            }
        )
    }

    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showCancelConfirmDialog = false
            },
            title = {
                Text(text = "Confirm Cancellation")
            },
            text = {
                Text("Are you sure you want to cancel and go back?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                        if (navController.currentBackStackEntry?.destination?.route == AppDestinations.ORDER_LIST_SCREEN) {
                            navController.popBackStack()
                        }
                    },
                    shape = buttonShape,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                    },
                    shape = buttonShape,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showCancelConfirmDialog = true },
                    shape = buttonShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { /* TODO: Handle Confirm action */ },
                    shape = buttonShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                ) {
                    Text("Confirm")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (orderItems.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your order list is empty.")
                }
                AddItemButtonInList(
                    onClick = { showItemSelectionDialog = true }, // Show the full-screen dialog
                    modifier = Modifier.height(96.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                ) {
                    items(orderItems.value, key = { item -> item.id }) { item ->
                        OrderItemRow(
                            item = item,
                            modifier = Modifier.height(144.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        AddItemButtonInList(
                            onClick = { showItemSelectionDialog = true }, // Show the full-screen dialog
                            modifier = Modifier.height(96.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Added consistent spacing
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemButtonInList(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Ensure consistent padding
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add new item", modifier = Modifier.padding(end = 8.dp))
        Text("Add Item")
    }
}

@Composable
fun OrderItemRow(
    item: OrderItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Qty: ${item.quantity} x $${"%.2f".format(item.price)}", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "$${"%.2f".format(item.getTotalPrice())}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    NoodleMoneyBuddyTheme {
        MenuScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun FullScreenItemSelectionDialogPreview() {
    NoodleMoneyBuddyTheme {
        // To preview the dialog effectively, you might need a host that allows dialogs to overlay.
        // This basic preview will render its content.
        Box(Modifier.fillMaxSize()) { // Simulate a screen context
            FullScreenItemSelectionDialog(
                onDismissRequest = {},
                onItemSelected = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderListScreenPreview() {
    NoodleMoneyBuddyTheme {
        OrderListScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun OrderListScreenEmptyPreview() {
    NoodleMoneyBuddyTheme {
        OrderListScreen(
            navController = rememberNavController()
        )
    }
}