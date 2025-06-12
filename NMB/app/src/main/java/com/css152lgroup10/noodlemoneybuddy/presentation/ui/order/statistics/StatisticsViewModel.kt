package com.css152lgroup10.noodlemoneybuddy.presentation.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.css152lgroup10.noodlemoneybuddy.data.model.SalesStatistics
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewModel(
    private val repository: IOrderRepository
) : ViewModel() {

    private val _statistics = MutableStateFlow<SalesStatistics?>(null)
    val statistics: StateFlow<SalesStatistics?> = _statistics

    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val endDate = formatter.format(today.time)
        today.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = formatter.format(today.time)

        viewModelScope.launch {
            repository.getSalesStatistics(startDate, endDate).onSuccess {
                _statistics.value = it
            }
        }
    }

    fun onExportClick() {
        _showExportDialog.value = true
    }

    fun onExportDismiss() {
        _showExportDialog.value = false
    }

    fun exportStatistics(startDate: String, endDate: String) {
        viewModelScope.launch {
            repository.exportSalesStatisticsToCSV(startDate, endDate)
        }
        _showExportDialog.value = false
    }
}
