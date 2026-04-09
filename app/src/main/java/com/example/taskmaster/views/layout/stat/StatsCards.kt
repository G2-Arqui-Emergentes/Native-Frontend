package com.example.taskmaster.views.layout.stat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.projects.ProjectStats

@Composable
fun StatsCards(stats: ProjectStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Total",
            value = stats.totalTasks.toString(),
            icon = R.drawable.ic_total_task,
            showTasks = true,
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
        )

        StatCard(
            title = "Vencidas",
            value = stats.overdueTasks.toString(),
            icon = R.drawable.ic_overdue_task,
            showTasks = true,
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
        )

        StatCard(
            title = "Mejor miembro",
            value = stats.bestMember,
            icon = R.drawable.ic_profile_placeholder2,
            showTasks = false,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Int,
    showTasks: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = androidx.compose.ui.graphics.Color.Unspecified
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (showTasks) "$value Tareas" else value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
