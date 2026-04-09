package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.net.ApiFactory
import com.example.taskmaster.viewmodel.data.net.NotificationsApi
import com.example.taskmaster.viewmodel.data.notifications.NotificationDto

class NotificationRepository(
    private val api: NotificationsApi = ApiFactory.notifications
) {
    // GET /api/v1/notifications/me
    suspend fun getMyNotifications(): List<NotificationDto> = api.getMyNotifications()
}