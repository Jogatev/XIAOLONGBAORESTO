package com.css152lgroup10.noodlemoneybuddy

// remove the duplicates of library imports later
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // Import NavController
import androidx.navigation.compose.NavHost // Import NavHost
import androidx.navigation.compose.composable // Import composable
import androidx.navigation.compose.rememberNavController // Import rememberNavController
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme
import androidx.navigation.NavOptionsBuilder // Import NavOptionsBuilder
import androidx.navigation.navOptions // Import navOptions helper

// Define route names as constants for better management
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ORDER_LIST_SCREEN = "order_list"
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
            onClick = { navController.navigate(AppDestinations.ORDER_LIST_SCREEN) },
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

@Composable
fun OrderListScreen(
    navController: NavController, // Added NavController for potential back navigation
    modifier: Modifier = Modifier
) {
    val buttonShape = RoundedCornerShape(8.dp) // Consistent button shape

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        // verticalArrangement = Arrangement.SpaceBetween // This also works to push to bottom
    ) {
        // Content for the Order List will go here (e.g., a LazyColumn of orders)
        Text(
            text = "This is the Order List Screen.",
            modifier = Modifier.align(Alignment.CenterHorizontally) // Center the text
        )

        Spacer(modifier = Modifier.weight(1f)) // This Spacer pushes the Row to the bottom

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // Some padding around the button row
            horizontalArrangement = Arrangement.End, // Aligns buttons to the right (end) of the Row
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // Handle Cancel action, e.g., navigate back
                    navController.popBackStack() // Example: Go back to the previous screen
                },
                shape = buttonShape,
                modifier = Modifier.weight(1f) // Give equal weight or adjust as needed
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between Cancel and Confirm buttons

            Button(
                onClick = { /* TODO: Handle Confirm action */ },
                shape = buttonShape,
                modifier = Modifier.weight(1f) // Give equal weight or adjust as needed
            ) {
                Text("Confirm")
            }
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
        OrderListScreen(navController = rememberNavController())
    }
}