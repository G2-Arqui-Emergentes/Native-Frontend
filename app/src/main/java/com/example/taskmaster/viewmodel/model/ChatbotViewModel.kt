package com.example.taskmaster.viewmodel.model

import androidx.lifecycle.ViewModel
import com.example.taskmaster.viewmodel.data.chatbot.ChatSender
import com.example.taskmaster.viewmodel.data.chatbot.ChatUiMessage
import com.example.taskmaster.viewmodel.data.repo.ChatbotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatbotViewModel(
    private val repo: ChatbotRepository = ChatbotRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _messages = MutableStateFlow<List<ChatUiMessage>>(emptyList())
    val messages: StateFlow<List<ChatUiMessage>> = _messages

    private val _lastFailedMessage = MutableStateFlow<String?>(null)
    val lastFailedMessage: StateFlow<String?> = _lastFailedMessage

    fun sendMessage(rawMessage: String) = launchCatching(_isLoading, _error) {
        val message = rawMessage.trim()
        if (message.isBlank()) return@launchCatching

        _messages.value = _messages.value + ChatUiMessage(
            text = message,
            sender = ChatSender.USER
        )
        _lastFailedMessage.value = null

        runCatching { repo.sendMessage(message) }
            .onSuccess { response ->
                _messages.value = _messages.value + ChatUiMessage(
                    text = response.response,
                    sender = ChatSender.BOT
                )
                _error.value = null
            }
            .onFailure { throwable ->
                _error.value = throwable.message ?: "Unable to get a response."
                _lastFailedMessage.value = message
                _messages.value = _messages.value + ChatUiMessage(
                    text = throwable.message ?: "No se pudo obtener respuesta. Intenta nuevamente.",
                    sender = ChatSender.BOT,
                    isError = true
                )
            }
    }

    fun retryLastMessage() {
        val failed = _lastFailedMessage.value ?: return
        sendMessage(failed)
    }

    fun clearError() {
        _error.value = null
    }
}
