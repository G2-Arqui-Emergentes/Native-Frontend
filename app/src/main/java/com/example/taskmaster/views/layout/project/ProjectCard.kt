package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.taskmaster.R
import com.example.taskmaster.viewmodel.data.projects.ProjectDto

@Composable
fun ProjectCard(
    project: ProjectDto,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageModel = if (project.imageUrl.isNullOrBlank()) {
                R.drawable.taskmaster_logoblanco
            } else {
                project.imageUrl
            }


            AsyncImage(
                model = imageModel,
                contentDescription = null,
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(22.dp))
            )
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
            ) {
                Text(project.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    project.description ,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Text(
                text = project.key,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}