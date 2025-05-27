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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme

// Define route names as constants for better management
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val CREATE_ORDER_SCREEN = "create_order"
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
                        // Define global transitions or override per composable
                        enterTransition = {
                            // Example: Slide in from the right, faster
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(durationMillis = 150) // Default is 300ms
                            ) + fadeIn(animationSpec = tween(durationMillis = 150))
                        },
                        exitTransition = {
                            // Example: Slide out to the left, faster
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(durationMillis = 150)
                            ) + fadeOut(animationSpec = tween(durationMillis = 150))
                        },
                        popEnterTransition = {
                            // Example: When popping back, slide in from left, faster
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(durationMillis = 150)
                            ) + fadeIn(animationSpec = tween(durationMillis = 150))
                        },
                        popExitTransition = {
                            // Example: When popping back, slide out to right, faster
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(durationMillis = 150)
                            ) + fadeOut(animationSpec = tween(durationMillis = 150))
                        }
                    ) {
                        composable(AppDestinations.MENU_SCREEN) {
                            MenuScreen(navController = navController)
                        }
                        composable(
                            route = AppDestinations.CREATE_ORDER_SCREEN,
                            // You can also override transitions for a specific destination
                            // enterTransition = { ... },
                            // exitTransition = { ... }
                        ) {
                            EmptyScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    navController: NavController, // Receive NavController
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
                // Navigate to the create order screen
                navController.navigate(AppDestinations.CREATE_ORDER_SCREEN)
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text("Create Order")
        }

        Button(
            onClick = { /* TODO: Handle Modify Order click */ },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text("Modify Order")
        }

        Button(
            onClick = { /* TODO: Handle View Statistics click */ },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(vertical = 8.dp)
        ) {
            Text("View Statistics")
        }
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This is the new empty screen for Create Order.")
    }
}


@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    NoodleMoneyBuddyTheme {
        // For preview, you can pass a dummy NavController or handle it differently if needed
        // For simplicity, this preview won't actually navigate.
        MenuScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyScreenPreview() {
    NoodleMoneyBuddyTheme {
        EmptyScreen()
    }
}