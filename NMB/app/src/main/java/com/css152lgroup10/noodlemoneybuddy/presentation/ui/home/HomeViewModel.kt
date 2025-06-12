package com.css152lgroup10.noodlemoneybuddy.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.css152lgroup10.noodlemoneybuddy.data.model.SalesStatistics
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val orderRepository: IOrderRepository
) : ViewModel() {

    private val _salesStatistics = MutableStateFlow<SalesStatistics?>(null)
    val salesStatistics: StateFlow<SalesStatistics?> = _salesStatistics

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadSalesStatistics(startDate: String, endDate: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = orderRepository.getSalesStatistics(startDate, endDate)
            _salesStatistics.value = result.getOrNull()
            _loading.value = false
        }
    }
}
