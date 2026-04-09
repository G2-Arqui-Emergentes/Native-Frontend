package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


fun ViewModel.launchCatching(
    isLoading: MutableStateFlow<Boolean>,
    error: MutableStateFlow<String?>,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch {
        try {
            isLoading.value = true
            error.value = null
            block(this)
        } catch (e: Exception) {
            error.value = e.message ?: "Error desconocido"
        } finally {
            isLoading.value = false
        }
    }
}
