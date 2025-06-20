package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.ui.theme.*
import com.css152lgroup10.noodlemoneybuddy.utils.AppDestinations
import com.css152lgroup10.noodlemoneybuddy.utils.ClickDebouncer
import com.css152lgroup10.noodlemoneybuddy.ui.components.*
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt

data class MenuOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: String,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun MenuScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val debouncer = remember { ClickDebouncer() }
    var isVisible by remember { mutableStateOf(false) }
    
    val menuOptions = remember {
        listOf(
            MenuOption(
                title = "Create Order",
                subtitle = "Start a new order",
                icon = Icons.Default.Add,
                destination = AppDestinations.ORDER_LIST_SCREEN,
                color = androidx.compose.ui.graphics.Color.Red
            ),
            MenuOption(
                title = "Order Records",
                subtitle = "View and modify orders",
                icon = Icons.Default.Receipt,
                destination = AppDestinations.ORDER_RECORDS_SCREEN,
                color = androidx.compose.ui.graphics.Color.Green
            ),
            MenuOption(
                title = "Statistics",
                subtitle = "View sales analytics",
                icon = Icons.Default.Info,
                destination = AppDestinations.STATISTICS_SCREEN,
                color = androidx.compose.ui.graphics.Color.Blue
            )
        )
    }

    // Trigger animations on first composition
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient with breathing animation
        BreathingAnimation {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App title with morphing animation
            MorphingAnimation(
                visible = isVisible
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PulseAnimation {
                        Text(
                            text = "XIAOLONGBAO",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = "Restaurant Management",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
            
            // Menu options without animation for debugging
            Column {
                menuOptions.forEachIndexed { index, option ->
                    FloatingAnimation {
                        MenuOptionCard(
                            option = option,
                            onClick = {
                                debouncer.processClick {
                                    navController.navigate(option.destination) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuOptionCard(
    option: MenuOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    RippleAnimation(
        isPressed = isPressed
    ) {
        Card(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isPressed) 2.dp else 8.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with background and pulse animation
                PulseAnimation {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(option.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.title,
                            tint = option.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = option.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Arrow icon with floating animation
                FloatingAnimation {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    
    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
} 