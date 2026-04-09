package com.example.taskmaster.views.layout.stat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskmaster.viewmodel.data.projects.ProjectStats

@Composable
fun TaskStatusOverview(stats: ProjectStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Estado de las tareas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusItem(
                    color = Color(0xFFA62424),
                    status = "Por hacer",
                    count = stats.todoTasks
                )

                StatusItem(
                    color = Color(0xFFF9E364),
                    status = "En proceso",
                    count = stats.inProgressTasks
                )

                StatusItem(
                    color = Color(0xFF82F687),
                    status = "Terminadas",
                    count = stats.doneTasks
                )
            }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(stats = stats)
            }
        }
    }
}

@Composable
fun StatusItem(
    color: Color,
    status: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "$count tareas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DonutChart(stats: ProjectStats, modifier: Modifier = Modifier) {
    val total = stats.todoTasks + stats.inProgressTasks + stats.doneTasks
    val sections = listOf(
        Pair(stats.todoTasks, Color(0xFFA62424)),
        Pair(stats.inProgressTasks, Color(0xFFF9E364)),
        Pair(stats.doneTasks, Color(0xFF82F687))
    )

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            var startAngle = -90f
            sections.forEach { (count, color) ->
                val sweep = if (total > 0) (count / total.toFloat()) * 360f else 0f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 36f)
                )
                startAngle += sweep
            }
        }
    }
}
