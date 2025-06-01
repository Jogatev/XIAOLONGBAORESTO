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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme
import kotlinx.coroutines.delay


// Data class (if not in a separate file)
data class OrderItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double
) {
    fun getTotalPrice(): Double = quantity * price
}

// Define route names as constants for better management
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ORDER_LIST_SCREEN = "order_list"
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
                        // ... (your existing transitions)
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
                            // Pass NavController if Cancel needs to navigate back, for example
                            OrderListScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

/* This screen comprises the:
- Create Order button
- Modify Order button
- View Statistics button
 */
@Composable
fun MenuScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val lessRoundedButtonShape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navController.navigate(AppDestinations.ORDER_LIST_SCREEN) {
                    launchSingleTop = true
                    // Optionally, you can also use popUpTo to clear previous instances
                    // if that's the desired behavior, but launchSingleTop is usually sufficient
                    // for preventing simple double-tap duplicates.
                    // popUpTo(AppDestinations.MENU_SCREEN) // Example if you want to pop back to menu before navigating
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
                .weight(0.5f) // This makes the button smaller compared to the previous 2 buttons
                .padding(vertical = 8.dp)
        ) { Text("View Statistics") }
    }
}

// This screen is where the user can add items to the customer's order list.
@Composable
fun OrderListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp) // Consistent button shape
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    val orderItems = remember {
        mutableStateOf(
            listOf(
                OrderItem("1", "Noodles A", 2, 5.00),
                OrderItem("2", "Drink B", 1, 2.50)
            )
        )
    }

    // Confirmation Dialog for Cancel
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
                    shape = buttonShape, // Use consistent shape
                    modifier = Modifier
                        .padding(horizontal = 8.dp) // Padding for dialog buttons
                        .defaultMinSize(minHeight = 48.dp) // Minimum touch target
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                    },
                    shape = buttonShape, // Use consistent shape
                    modifier = Modifier
                        .padding(horizontal = 8.dp) // Padding for dialog buttons
                        .defaultMinSize(minHeight = 48.dp) // Minimum touch target
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
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Padding for the Row
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showCancelConfirmDialog = true }, // Show dialog
                    shape = buttonShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp) // Set height for bottom bar buttons
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(16.dp)) // Space between buttons

                Button(
                    onClick = { /* TODO: Handle Confirm action */ },
                    shape = buttonShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp) // Set height for bottom bar buttons
                ) {
                    Text("Confirm")
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
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your order list is empty.")
                }
                // "Add Item" button for empty state
                AddItemButtonInList(
                    onClick = {
                        val newItemId = (orderItems.value.size + 1).toString()
                        orderItems.value = orderItems.value + OrderItem(newItemId, "New Item $newItemId", 1, 1.00)
                    },
                    modifier = Modifier.height(96.dp) // Height for AddItemButton
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f), // LazyColumn takes available space
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                ) {
                    items(orderItems.value, key = { item -> item.id }) { item ->
                        OrderItemRow(
                            item = item,
                            modifier = Modifier.height(144.dp) // Height for OrderItemRow
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // "Add Item" button at the end of the list
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        AddItemButtonInList(
                            onClick = {
                                val newItemId = (orderItems.value.size + 1).toString()
                                orderItems.value = orderItems.value + OrderItem(newItemId, "New Item $newItemId", 1, 1.00)
                            },
                            modifier = Modifier.height(96.dp) // Height for AddItemButton
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// AddItemButtonInList Composable
@Composable
fun AddItemButtonInList(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier // Apply the passed modifier (which includes height)
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Keep existing internal padding or adjust
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add new item", modifier = Modifier.padding(end = 8.dp))
        Text("Add Item")
    }
}

// OrderItemRow Composable
@Composable
fun OrderItemRow(
    item: OrderItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier // Apply the passed modifier (which includes height)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(), // Allow content to fill the new card height
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
fun OrderListScreenPreview() {
    NoodleMoneyBuddyTheme {
        val navController = rememberNavController()
        OrderListScreen(navController = navController)
    }
}