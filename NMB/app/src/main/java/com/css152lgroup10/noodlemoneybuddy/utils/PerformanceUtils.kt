package com.css152lgroup10.noodlemoneybuddy.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

// Debounced state for search and input
@Composable
fun rememberDebouncedState(
    initialValue: String,
    delayMillis: Long = 300L
): State<String> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    var debouncedValue by remember { mutableStateOf(initialValue) }
    var lastValue by remember { mutableStateOf(initialValue) }
    
    LaunchedEffect(lastValue) {
        delay(delayMillis)
        debouncedValue = lastValue
    }
    
    return remember {
        derivedStateOf { debouncedValue }
    }
}

// Throttled state for frequent updates
@Composable
fun rememberThrottledState(
    initialValue: String,
    throttleMillis: Long = 100L
): State<String> {
    val scope = rememberCoroutineScope()
    var throttledValue by remember { mutableStateOf(initialValue) }
    var lastUpdateTime by remember { mutableStateOf(0L) }
    
    return remember {
        derivedStateOf { throttledValue }
    }
}

// Lazy loading state
@Composable
fun rememberLazyLoadingState(
    initialData: List<Any> = emptyList(),
    loadMore: suspend () -> List<Any>
): LazyLoadingState {
    val scope = rememberCoroutineScope()
    var data by remember { mutableStateOf(initialData) }
    var isLoading by remember { mutableStateOf(false) }
    var hasMore by remember { mutableStateOf(true) }
    
    val loadMoreData = remember {
        {
            if (!isLoading && hasMore) {
                scope.launch {
                    isLoading = true
                    try {
                        val newData = loadMore()
                        if (newData.isEmpty()) {
                            hasMore = false
                        } else {
                            data = data + newData
                        }
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            }
        }
    }
    
    return remember {
        LazyLoadingState(
            data = data,
            isLoading = isLoading,
            hasMore = hasMore,
            loadMore = loadMoreData
        )
    }
}

data class LazyLoadingState(
    val data: List<Any>,
    val isLoading: Boolean,
    val hasMore: Boolean,
    val loadMore: () -> Unit
)

// Memory efficient list operations
object ListUtils {
    fun <T> chunkedList(list: List<T>, chunkSize: Int): List<List<T>> {
        return list.chunked(chunkSize)
    }
    
    fun <T> filterAndMapEfficiently(
        list: List<T>,
        filter: (T) -> Boolean,
        map: (T) -> Any
    ): List<Any> {
        return list.asSequence()
            .filter(filter)
            .map(map)
            .toList()
    }
}

// Coroutine scope with lifecycle awareness
@Composable
fun rememberLifecycleAwareCoroutineScope(): CoroutineScope {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    scope.cancel()
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    return scope
}

// Cached computation
@Composable
fun <T> rememberCachedComputation(
    key: Any,
    computation: () -> T
): T {
    return remember(key) {
        computation()
    }
}

// Optimized recomposition
@Composable
fun <T> rememberOptimizedState(
    initialValue: T,
    key: Any? = null
): State<T> {
    return remember(key) {
        mutableStateOf(initialValue)
    }
}

// Background task executor
object BackgroundExecutor {
    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    fun execute(
        onStart: () -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {},
        task: suspend () -> Unit
    ) {
        backgroundScope.launch {
            try {
                onStart()
                task()
                onComplete()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
    
    fun cancelAll() {
        backgroundScope.cancel()
    }
}

// Memory cache for expensive computations
class MemoryCache<K, V>(
    private val maxSize: Int = 100
) {
    private val cache = mutableMapOf<K, V>()
    
    fun get(key: K): V? {
        return cache[key]
    }
    
    fun put(key: K, value: V) {
        if (cache.size >= maxSize) {
            val firstKey = cache.keys.first()
            cache.remove(firstKey)
        }
        cache[key] = value
    }
    
    fun clear() {
        cache.clear()
    }
    
    fun size(): Int = cache.size
}

// Lazy initialization
object LazyInitializer {
    private val initialized = mutableSetOf<String>()
    
    fun <T> initialize(
        key: String,
        initializer: () -> T
    ): T? {
        return if (initialized.contains(key)) {
            null
        } else {
            initialized.add(key)
            initializer()
        }
    }
    
    fun reset(key: String) {
        initialized.remove(key)
    }
    
    fun resetAll() {
        initialized.clear()
    }
}

// Performance monitoring
object PerformanceMonitor {
    private val timings = mutableMapOf<String, Long>()
    
    fun startTimer(key: String) {
        timings[key] = System.currentTimeMillis()
    }
    
    fun endTimer(key: String): Long? {
        val startTime = timings.remove(key)
        return startTime?.let { System.currentTimeMillis() - it }
    }
    
    fun getTiming(key: String): Long? {
        return timings[key]
    }
    
    fun clearTimings() {
        timings.clear()
    }
}

// Optimized list operations
object OptimizedListOps {
    fun <T> findFirstOrNull(
        list: List<T>,
        predicate: (T) -> Boolean
    ): T? {
        return list.asSequence().find(predicate)
    }
    
    fun <T> takeWhileOptimized(
        list: List<T>,
        predicate: (T) -> Boolean
    ): List<T> {
        return list.asSequence().takeWhile(predicate).toList()
    }
    
    fun <T> distinctByOptimized(
        list: List<T>,
        selector: (T) -> Any
    ): List<T> {
        return list.asSequence().distinctBy(selector).toList()
    }
} 