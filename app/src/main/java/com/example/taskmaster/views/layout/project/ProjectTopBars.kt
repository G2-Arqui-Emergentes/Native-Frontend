package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ProjectSection { TASKS, STATS, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectTopBar(
    title: String,
    onBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Volver")
            }
        },
        title = {
            Text(title.ifBlank { "Proyecto" }, style = MaterialTheme.typography.titleLarge)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}

@Composable
fun ProjectMiniTabs(
    selected: ProjectSection,
    onSelect: (ProjectSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabItem("Tareas",       selected == ProjectSection.TASKS)      { onSelect(ProjectSection.TASKS) }
        TabItem("Estadísticas", selected == ProjectSection.STATS)      { onSelect(ProjectSection.STATS) }
        TabItem("Ajustes",      selected == ProjectSection.SETTINGS)   { onSelect(ProjectSection.SETTINGS) }
    }
}

@Composable
private fun TabItem(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
        ),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.clickable(onClick = onClick)
    )
}
