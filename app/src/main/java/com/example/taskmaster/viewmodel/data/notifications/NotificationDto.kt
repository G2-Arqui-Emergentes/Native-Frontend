package com.example.taskmaster.viewmodel.data.notifications

data class NotificationDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val message: String,
    val sentAt: String
)