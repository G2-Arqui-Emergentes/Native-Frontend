package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.taskmaster.viewmodel.data.projects.ProjectDto

@Composable
fun ProjectsList(
    projects: List<ProjectDto>,
    onProjectClick: (ProjectDto) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = projects,
            key = { it.projectId }
        ) { p ->
            ProjectCard(project = p) {
                onProjectClick(p)
            }
        }
    }
}
