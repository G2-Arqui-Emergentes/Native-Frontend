package com.example.taskmaster.views.layout.notification

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.views.layout.common.AppTopHeader
import com.example.taskmaster.viewmodel.model.NotificationsViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import java.util.Locale

private val NotificationsBackground = Color(0xFFF9FAFB)
private val NotificationsSurface = Color(0xFFFFFFFF)
private val NotificationsField = Color(0xFFF4F5F7)
private val NotificationsBorder = Color(0xFFE5E7EB)
private val NotificationsTextPrimary = Color(0xFF111827)
private val NotificationsTextSecondary = Color(0xFF6B7280)
private val NotificationsBrand = Color(0xFFEC1926)

@Composable
fun Notifiations(
    context: Context,
    nav: NavHostController,
    userVm: UsersViewModel = remember { UsersViewModel() },
    notificationsVm: NotificationsViewModel = remember { NotificationsViewModel() }
) {
    val user by userVm.user.collectAsState()
    val userLoading by userVm.isLoading.collectAsState()
    val notifications by notificationsVm.notifications.collectAsState()
    val notificationsLoading by notificationsVm.isLoading.collectAsState()
    val notificationsError by notificationsVm.error.collectAsState()

    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    val isSpanish = locale.language.startsWith("es")

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
        notificationsVm.loadMyNotifications()
    }

    val filteredNotifications = remember(notifications, searchQuery) {
        if (searchQuery.isBlank()) {
            notifications
        } else {
            notifications.filter { notification ->
                val searchable = buildString {
                    append(notification.title)
                    append(' ')
                    append(notification.message)
                }
                searchable.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NotificationsBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            AppTopHeader(
                user = user,
                onNotificationsClick = {},
                onProfileClick = { nav.navigate("profile") }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isSpanish) "Notificaciones" else "Notifications",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = NotificationsTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = {
                    Text(
                        text = if (isSpanish) "Buscar notificación" else "Search notification",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NotificationsTextSecondary
                    )
                },
                trailingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = if (isSpanish) "Buscar" else "Search",
                        tint = NotificationsTextSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NotificationsField,
                    unfocusedContainerColor = NotificationsField,
                    disabledContainerColor = NotificationsField,
                    focusedBorderColor = NotificationsBrand.copy(alpha = 0.25f),
                    unfocusedBorderColor = NotificationsBorder,
                    disabledBorderColor = NotificationsBorder,
                    focusedTextColor = NotificationsTextPrimary,
                    unfocusedTextColor = NotificationsTextPrimary,
                    cursorColor = NotificationsBrand
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                (userLoading && user == null) || (notificationsLoading && notifications.isEmpty()) -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NotificationsBrand)
                    }
                }

                !notificationsError.isNullOrBlank() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = NotificationsSurface)
                    ) {
                        Text(
                            text = notificationsError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }

                notifications.isEmpty() -> {
                    NotificationsEmptyState(
                        text = if (isSpanish) {
                            "No tienes notificaciones todavía."
                        } else {
                            "No notifications yet."
                        }
                    )
                }

                filteredNotifications.isEmpty() -> {
                    NotificationsEmptyState(
                        text = if (isSpanish) {
                            "No se encontraron notificaciones."
                        } else {
                            "No notifications found."
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = filteredNotifications,
                            key = { it.id }
                        ) { notification ->
                            NotificationCard(
                                notification = notification,
                                isSpanish = isSpanish
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsEmptyState(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NotificationsSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularNotificationBadge()
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NotificationsTextSecondary
                )
            }
        }
    }
}

@Composable
private fun CircularNotificationBadge() {
    Box(
        modifier = Modifier
            .size(58.dp)
            .background(
                color = NotificationsBrand.copy(alpha = 0.10f),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = NotificationsBrand
        )
    }
}
