package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.graphics.graphicsLayer

data class MenuOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: String,
    val primaryColor: Color,
    val secondaryColor: Color
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
                subtitle = "Start a new order and manage items",
                icon = Icons.Default.Add,
                destination = AppDestinations.ORDER_LIST_SCREEN,
                primaryColor = Color(0xFF6366F1), // Indigo
                secondaryColor = Color(0xFF8B5CF6)  // Purple
            ),
            MenuOption(
                title = "Order Records",
                subtitle = "View history and modify orders",
                icon = Icons.Default.Receipt,
                destination = AppDestinations.ORDER_RECORDS_SCREEN,
                primaryColor = Color(0xFF10B981), // Emerald
                secondaryColor = Color(0xFF06B6D4)  // Cyan
            ),
            MenuOption(
                title = "Statistics",
                subtitle = "View detailed sales analytics",
                icon = Icons.Default.Info,
                destination = AppDestinations.STATISTICS_SCREEN,
                primaryColor = Color(0xFFF59E0B), // Amber
                secondaryColor = Color(0xFFEF4444)  // Red
            )
        )
    }

    // Trigger animations on first composition
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Enhanced background with dynamic gradient
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
        
        // Decorative elements
        DecorativeElements()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header section with enhanced styling
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(800, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo placeholder with gradient background
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFF8B5CF6)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🥟",
                            fontSize = 36.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    PulseAnimation {
                        Text(
                            text = "XIAOLONGBAO",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Restaurant Management System",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Menu options with staggered animation
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                menuOptions.forEachIndexed { index, option ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { if (index % 2 == 0) -it else it },
                            animationSpec = tween(
                                durationMillis = 600,
                                delayMillis = 300 + (index * 150),
                                easing = EaseOutCubic
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 600,
                                delayMillis = 300 + (index * 150)
                            )
                        )
                    ) {
                        EnhancedMenuOptionCard(
                            option = option,
                            onClick = {
                                debouncer.processClick {
                                    navController.navigate(option.destination) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
            
            // Footer with subtle branding
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 800,
                        delayMillis = 1000
                    )
                )
            ) {
                Text(
                    text = "Powered by NoodleMoneyBuddy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EnhancedMenuOptionCard(
    option: MenuOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 12.dp,
        animationSpec = tween(200),
        label = "elevation"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )
    
    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(20.dp),
                ambientColor = option.primaryColor.copy(alpha = 0.3f),
                spotColor = option.primaryColor.copy(alpha = 0.3f)
            )
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gradient accent strip
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                option.primaryColor,
                                option.secondaryColor
                            )
                        )
                    )
            )
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced icon with gradient background
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    option.primaryColor.copy(alpha = 0.1f),
                                    option.secondaryColor.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.title,
                        tint = option.primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                // Enhanced text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = option.subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Animated arrow with gradient
                FloatingAnimation {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        option.primaryColor.copy(alpha = 0.1f),
                                        option.secondaryColor.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Navigate",
                            tint = option.primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
    
    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200)
            isPressed = false
        }
    }
}

@Composable
fun DecorativeElements() {
    // Floating decorative circles
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top-right circle
        FloatingAnimation {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 300.dp, y = (-60).dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
            )
        }
        
        // Bottom-left circle
        FloatingAnimation {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = (-40).dp, y = 500.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
            )
        }
        
        // Middle decorative element
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 200.dp, y = 300.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}