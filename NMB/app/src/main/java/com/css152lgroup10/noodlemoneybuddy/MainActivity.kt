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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme


// Data class for Order Items
data class OrderItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double
) {
    fun getTotalPrice(): Double = quantity * price
}

// Define route names as constants
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ORDER_LIST_SCREEN = "order_list"
    const val ITEM_SELECTION_SCREEN = "item_selection"
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
                            OrderListScreen(navController = navController)
                        }
                        composable(AppDestinations.ITEM_SELECTION_SCREEN) {
                            ItemSelectionScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuScreen( // Reverted to previous state without explicit doubled height
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
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Using weight for proportional height
                .padding(vertical = 8.dp)
        ) { Text("Create Order") }

        Button(
            onClick = { /* TODO: Handle Modify Order click */ },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Using weight for proportional height
                .padding(vertical = 8.dp)
        ) { Text("Modify Order") }

        Button(
            onClick = { /* TODO: Handle View Statistics click */ },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f) // Using weight for proportional height (smaller)
                .padding(vertical = 8.dp)
        ) { Text("View Statistics") }
    }
}

@Composable
fun OrderListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp)
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    val orderItems = remember {
        mutableStateOf(
            listOf(
                OrderItem("1", "Noodles A (Sample)", 2, 5.00),
                OrderItem("2", "Drink B (Sample)", 1, 2.50)
            )
        )
    }

    val selectedItemName = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("selectedItemName")

    LaunchedEffect(selectedItemName) {
        if (selectedItemName != null) {
            val newItemId = (orderItems.value.size + 1).toString()
            val newItem = OrderItem(id = newItemId, name = selectedItemName, quantity = 1, price = 3.00) // Example price
            orderItems.value = orderItems.value + newItem
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedItemName")
        }
    }

    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            title = { Text(text = "Confirm Cancellation") },
            text = { Text("Are you sure you want to cancel and go back?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                        if (navController.currentBackStackEntry?.destination?.route == AppDestinations.ORDER_LIST_SCREEN) {
                            navController.popBackStack()
                        }
                    },
                    shape = buttonShape,
                    modifier = Modifier.padding(horizontal = 8.dp).defaultMinSize(minHeight = 48.dp)
                ) { Text("Yes") }
            },
            dismissButton = {
                Button(
                    onClick = { showCancelConfirmDialog = false },
                    shape = buttonShape,
                    modifier = Modifier.padding(horizontal = 8.dp).defaultMinSize(minHeight = 48.dp)
                ) { Text("No") }
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
                    modifier = Modifier.weight(1f).height(60.dp)
                ) { Text("Cancel") }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { /* TODO: Handle Confirm action */ },
                    shape = buttonShape,
                    modifier = Modifier.weight(1f).height(60.dp)
                ) { Text("Confirm") }
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
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your order list is empty.")
                }
                AddItemButtonInList(
                    onClick = {
                        navController.navigate(AppDestinations.ITEM_SELECTION_SCREEN) {
                            launchSingleTop = true
                        }
                    },
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
                            onClick = {
                                navController.navigate(AppDestinations.ITEM_SELECTION_SCREEN) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.height(96.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
            .padding(vertical = 8.dp),
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

@Composable
fun ItemSelectionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val availableItems = listOf(
        "Noodle A" to 5.00,
        "Noodle B" to 6.50,
        "Drink X" to 2.00,
        "Side Y" to 3.25
    )
    // A typical Card height for a single line of text with padding might be around 56-72dp.
    // Doubling that would be around 112dp to 144dp. Let's use 120.dp for example.
    val doubledItemHeight = 120.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select an Item to Add",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(availableItems) { (itemName, itemPrice) -> // Destructure Pair
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(doubledItemHeight) // Apply doubled height to each item card
                        .padding(vertical = 4.dp) // Keep some vertical padding between cards
                        .clickable {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedItemName", itemName)
                            navController.popBackStack()
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(), // Ensure Row fills the Card's new height
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically // Center content within the taller card
                    ) {
                        Text(text = itemName, style = MaterialTheme.typography.titleMedium) // Optionally increase text size
                        Text(text = "$${"%.2f".format(itemPrice)}", style = MaterialTheme.typography.titleMedium) // Optionally increase text size
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space before cancel button

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth() // Cancel button full width
                .height(60.dp), // Specific height for cancel button
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Cancel")
        }
    }
}

// Previews
@Preview(showBackground = true, name = "Menu Screen")
@Composable
fun MenuScreenPreview() {
    NoodleMoneyBuddyTheme {
        MenuScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Order List Screen (Empty)")
@Composable
fun OrderListScreenEmptyPreview() {
    NoodleMoneyBuddyTheme {
        OrderListScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Order List Screen (With Items)")
@Composable
fun OrderListScreenWithItemsPreview() {
    NoodleMoneyBuddyTheme {
        OrderListScreen(navController = rememberNavController())
    }
}


@Preview(showBackground = true, name = "Item Selection Screen")
@Composable
fun ItemSelectionScreenPreview() {
    NoodleMoneyBuddyTheme {
        ItemSelectionScreen(navController = rememberNavController())
    }
}