// views/layout/notification/NotificationList.kt
package com.example.taskmaster.views.layout.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.model.NotificationsViewModel

@Composable
fun NotificationList(
    searchQuery: String,
    vm: NotificationsViewModel = remember { NotificationsViewModel() }
) {
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val notifications by vm.notifications.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyNotifications()
    }

    val filtered = remember(notifications, searchQuery) {
        if (searchQuery.isBlank()) notifications
        else notifications.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.message.contains(searchQuery, ignoreCase = true)
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator()
            }
        }

        !error.isNullOrBlank() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        notifications.isEmpty() -> {
            EmptyNotifications()
        }

        filtered.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No se encontraron notificaciones para \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filtered) { notif ->
                    NotificationCard(notification = notif) {
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_notifications),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .padding(top = 24.dp, bottom = 12.dp)
        )
        Text(
            text = "¡No cuentas con ninguna notificación!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
