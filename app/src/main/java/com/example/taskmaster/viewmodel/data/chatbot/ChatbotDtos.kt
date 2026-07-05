package com.example.taskmaster.viewmodel.data.chatbot

data class ChatbotRequest(
    val message: String
)

data class ChatbotResponse(
    val response: String,
    val context: String
)

enum class ChatSender {
    USER,
    BOT
}

data class ChatUiMessage(
    val text: String,
    val sender: ChatSender,
    val isError: Boolean = false
)
