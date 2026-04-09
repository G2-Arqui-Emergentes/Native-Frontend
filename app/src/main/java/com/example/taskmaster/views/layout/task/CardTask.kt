package com.example.taskmaster.views.layout.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority

@Composable
fun CardTask(
    task: TaskDto,
    assigneeAvatarUrl: String? = null,
    assigneeName: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val bg = when (task.priority) {
        TaskPriority.HIGH   -> Color(0xFFF1797A)
        TaskPriority.MEDIUM -> Color(0xFFFFED8C)
        TaskPriority.LOW    -> Color(0xFF8CFF90)
    }
    val prioridadText = when (task.priority) {
        TaskPriority.HIGH -> "Alta"
        TaskPriority.MEDIUM -> "Media"
        TaskPriority.LOW -> "Baja"
    }

    val assignedLabel = assigneeName ?: "Sin asignar"


    val content = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1.2f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Asignado", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (assigneeAvatarUrl.isNullOrBlank()) {
                        Image(
                            painter = painterResource(R.drawable.ic_user),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp).clip(CircleShape)
                        )
                    } else {
                        AsyncImage(
                            model = assigneeAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp).clip(CircleShape)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(assignedLabel, style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Prioridad", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Text(prioridadText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = bg),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) { content() }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = bg),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) { content() }
    }
}
