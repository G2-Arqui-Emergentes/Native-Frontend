package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.chatbot.ChatbotRequest
import com.example.taskmaster.viewmodel.data.chatbot.ChatbotResponse
import com.example.taskmaster.viewmodel.data.net.ApiFactory
import com.example.taskmaster.viewmodel.data.net.ChatbotApi

class ChatbotRepository(
    private val api: ChatbotApi = ApiFactory.chatbot
) {
    suspend fun sendMessage(message: String): ChatbotResponse {
        return api.sendMessage(ChatbotRequest(message = message))
    }
}
