package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.taskmaster.viewmodel.data.notifications.NotificationDto
import com.example.taskmaster.viewmodel.data.repo.NotificationRepository

class NotificationsViewModel(
    private val repo: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _notifications = MutableStateFlow<List<NotificationDto>>(emptyList())
    val notifications: StateFlow<List<NotificationDto>> = _notifications

    fun loadMyNotifications() = scope.launch {
        try {
            _isLoading.value = true
            _notifications.value = repo.getMyNotifications()
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
