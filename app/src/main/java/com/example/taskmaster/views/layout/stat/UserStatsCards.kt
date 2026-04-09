package com.example.taskmaster.views.layout.stat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmaster.views.layout.project.UserTaskStats

@Composable
fun UserStatsCards(stats: UserTaskStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UserStatCard(
            title = "Asignadas",
            value = stats.totalTasks.toString(),
            subtitle = "Tareas totales",
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
        )

        UserStatCard(
            title = "En progreso",
            value = stats.inProgressTasks.toString(),
            subtitle = "En curso",
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
        )

        UserStatCard(
            title = "Terminadas",
            value = stats.doneTasks.toString(),
            subtitle = "Completadas",
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
        )
    }
}
