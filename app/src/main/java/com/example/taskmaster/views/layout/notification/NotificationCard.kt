package com.example.taskmaster.views.layout.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmaster.viewmodel.data.notifications.NotificationDto
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Composable
fun NotificationCard(
    notification: NotificationDto,
    isSpanish: Boolean,
    onClick: () -> Unit = {}
) {
    val type = notificationType(notification)
    val accent = if (type == "projects") Color(0xFF0F9E6E) else Color(0xFFEC1926)
    val initials = notificationInitials(notification)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = accent
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = relativeTime(notification.sentAt, isSpanish),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

private fun notificationType(notification: NotificationDto): String {
    val content = "${notification.title} ${notification.message}".lowercase(Locale.getDefault())
    val projectKeywords = listOf("proyecto", "project", "miembro", "member", "ingreso", "unio", "joined")
    return if (projectKeywords.any { content.contains(it) }) "projects" else "tasks"
}

private fun notificationInitials(notification: NotificationDto): String {
    val words = "${notification.title} ${notification.message}"
        .split(Regex("\\s+"))
        .filter { it.any(Char::isLetter) }
        .take(2)

    if (words.isEmpty()) return "N"

    return words.joinToString("") { word ->
        word.firstOrNull { it.isLetter() }?.uppercaseChar()?.toString().orEmpty()
    }
}

private fun relativeTime(sentAt: String, isSpanish: Boolean): String {
    val instant = parseNotificationInstant(sentAt) ?: return sentAt
    val duration = Duration.between(instant, Instant.now())
    val minutes = duration.toMinutes().coerceAtLeast(0)
    val hours = duration.toHours().coerceAtLeast(0)
    val days = duration.toDays().coerceAtLeast(0)

    return when {
        minutes < 1 -> if (isSpanish) "Ahora mismo" else "Just now"
        minutes < 60 -> if (isSpanish) "Hace $minutes min" else "$minutes min ago"
        hours < 24 -> if (isSpanish) "Hace $hours h" else "$hours h ago"
        days < 7 -> if (isSpanish) "Hace $days d" else "$days d ago"
        else -> {
            val locale = if (isSpanish) Locale("es", "ES") else Locale.ENGLISH
            DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant)
        }
    }
}

private fun parseNotificationInstant(value: String): Instant? {
    return try {
        Instant.parse(value)
    } catch (_: DateTimeParseException) {
        try {
            OffsetDateTime.parse(value).toInstant()
        } catch (_: DateTimeParseException) {
            try {
                ZonedDateTime.parse(value).toInstant()
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }
}
