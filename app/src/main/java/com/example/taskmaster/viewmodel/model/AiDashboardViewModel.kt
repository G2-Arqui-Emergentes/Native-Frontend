package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import com.example.taskmaster.viewmodel.data.ai.LeaderDashboardDto
import com.example.taskmaster.viewmodel.data.repo.AiDashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AiDashboardViewModel(
    private val repo: AiDashboardRepository = AiDashboardRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _dashboard = MutableStateFlow<LeaderDashboardDto?>(null)
    val dashboard: StateFlow<LeaderDashboardDto?> = _dashboard

    fun loadLeaderDashboard() = launchCatching(_isLoading, _error) {
        _dashboard.value = repo.getLeaderDashboard()
    }
}
