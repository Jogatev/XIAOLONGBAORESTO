package com.css152lgroup10.noodlemoneybuddy.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Animation durations
object AnimationDurations {
    const val SHORT = 200
    const val MEDIUM = 300
    const val LONG = 500
    const val EXTRA_LONG = 800
}

// Spring animation configurations
object SpringConfigs {
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val snappy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    val smooth = spring<Float>(
        dampingRatio = 1f,
        stiffness = Spring.StiffnessLow
    )
}

// Fade in animation
@Composable
fun FadeInAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(AnimationDurations.MEDIUM)
        ),
        exit = fadeOut(
            animationSpec = tween(AnimationDurations.SHORT)
        ),
        content = content
    )
}

// Slide in animation
@Composable
fun SlideInAnimation(
    visible: Boolean,
    slideDirection: SlideDirection = SlideDirection.Up,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(AnimationDurations.MEDIUM),
            initialOffsetY = { if (slideDirection == SlideDirection.Up) it else -it }
        ) + fadeIn(animationSpec = tween(AnimationDurations.MEDIUM)),
        exit = slideOutVertically(
            animationSpec = tween(AnimationDurations.SHORT),
            targetOffsetY = { if (slideDirection == SlideDirection.Up) -it else it }
        ) + fadeOut(animationSpec = tween(AnimationDurations.SHORT)),
        content = content
    )
}

// Scale animation
@Composable
fun ScaleAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(AnimationDurations.MEDIUM),
            initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(AnimationDurations.MEDIUM)),
        exit = scaleOut(
            animationSpec = tween(AnimationDurations.SHORT),
            targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(AnimationDurations.SHORT)),
        content = content
    )
}

// Spring-based scale animation
@Composable
fun SpringScaleAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = SpringConfigs.bouncy,
            initialScale = 0.3f
        ) + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(
            animationSpec = SpringConfigs.snappy,
            targetScale = 0.3f
        ) + fadeOut(animationSpec = tween(200)),
        content = content
    )
}

// Staggered list animation
@Composable
fun StaggeredListAnimation(
    visible: Boolean,
    itemCount: Int,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
            animationSpec = tween(AnimationDurations.LONG),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurations.LONG,
                delayMillis = (itemCount * 50).coerceAtMost(500)
            )
        ),
        exit = shrinkVertically(
            animationSpec = tween(AnimationDurations.MEDIUM),
            shrinkTowards = Alignment.Top
        ) + fadeOut(animationSpec = tween(AnimationDurations.SHORT)),
        content = content
    )
}

// Loading animation with rotation
@Composable
fun LoadingAnimation(
    message: String = "Loading..."
) {
    var rotation by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            rotation += 360f
            delay(1000)
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = 1.2f,
                    scaleY = 1.2f,
                    rotationZ = rotation
                )
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Pulse animation for important elements
@Composable
fun PulseAnimation(
    content: @Composable () -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        while (true) {
            isAnimating = true
            delay(1000)
            isAnimating = false
            delay(2000)
        }
    }
    
    Box(
        modifier = Modifier.graphicsLayer(
            scaleX = if (isAnimating) 1.05f else 1f,
            scaleY = if (isAnimating) 1.05f else 1f,
            alpha = if (isAnimating) 0.8f else 1f
        )
    ) {
        content()
    }
}

// Shake animation for errors
@Composable
fun ShakeAnimation(
    shouldShake: Boolean,
    content: @Composable () -> Unit
) {
    var shakeOffset by remember { mutableStateOf(0f) }
    
    LaunchedEffect(shouldShake) {
        if (shouldShake) {
            repeat(3) {
                shakeOffset = 10f
                delay(100)
                shakeOffset = -10f
                delay(100)
                shakeOffset = 0f
                delay(100)
            }
        }
    }
    
    Box(
        modifier = Modifier.graphicsLayer(
            translationX = shakeOffset
        )
    ) {
        content()
    }
}

// Bounce animation for success
@Composable
fun BounceAnimation(
    shouldBounce: Boolean,
    content: @Composable () -> Unit
) {
    var bounceScale by remember { mutableStateOf(1f) }
    
    LaunchedEffect(shouldBounce) {
        if (shouldBounce) {
            bounceScale = 1.2f
            delay(150)
            bounceScale = 0.9f
            delay(150)
            bounceScale = 1f
        }
    }
    
    Box(
        modifier = Modifier.graphicsLayer(
            scaleX = bounceScale,
            scaleY = bounceScale
        )
    ) {
        content()
    }
}

// Floating animation for cards
@Composable
fun FloatingAnimation(
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )
    
    Box(
        modifier = Modifier.graphicsLayer(
            translationY = offset
        )
    ) {
        content()
    }
}

// Ripple animation for buttons
@Composable
fun RippleAnimation(
    isPressed: Boolean,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = SpringConfigs.snappy,
        label = "ripple_scale"
    )
    
    Box(
        modifier = Modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale
        )
    ) {
        content()
    }
}

// Morphing animation for transitions
@Composable
fun MorphingAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(400),
            initialOffsetY = { it }
        ) + scaleIn(
            animationSpec = SpringConfigs.bouncy,
            initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { -it }
        ) + scaleOut(
            animationSpec = SpringConfigs.snappy,
            targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(300)),
        content = content
    )
}

// Breathing animation for attention
@Composable
fun BreathingAnimation(
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )
    
    Box(
        modifier = Modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale
        )
    ) {
        content()
    }
}

enum class SlideDirection {
    Up, Down, Left, Right
} 