package com.example.taskmaster.views.layout.stat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskmaster.viewmodel.data.projects.ProjectStats
import kotlin.math.max

@Composable
fun PriorityChart(stats: ProjectStats) {
    val barColor = Color(0xFFb698cb)
    val maxBarHeight = 120.dp
    val barLabels = listOf("Alta", "Media", "Baja")
    val barValues = listOf(stats.highPriorityTasks, stats.mediumPriorityTasks, stats.lowPriorityTasks)
    val maxTasks = max(1, barValues.maxOrNull() ?: 1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Prioridad de las tareas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .align(Alignment.BottomStart),
                verticalArrangement = Arrangement.Bottom
            ) {
                for (i in maxTasks downTo 0) {
                    val stepHeight = maxBarHeight / maxTasks
                    Row(
                        modifier = Modifier.height(stepHeight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = i.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(24.dp),
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Canvas(modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)) {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.5f),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 36.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                barValues.zip(barLabels).forEach { (count, label) ->
                    val barHeight = if (maxTasks > 0) (count / maxTasks.toFloat()) * maxBarHeight.value else 0f

                    Column(
                        modifier = Modifier.width(38.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .height(maxBarHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (barHeight > 0f) {
                                Canvas(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(barHeight.dp)
                                ) {
                                    drawRoundRect(
                                        color = barColor,
                                        size = size,
                                        cornerRadius = CornerRadius(10f, 10f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
