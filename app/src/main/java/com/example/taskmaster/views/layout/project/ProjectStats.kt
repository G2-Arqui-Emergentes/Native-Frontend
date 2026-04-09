package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.viewmodel.model.ProjectStatsViewModel
import com.example.taskmaster.viewmodel.model.ProjectsViewModel
import com.example.taskmaster.views.layout.stat.PriorityChart
import com.example.taskmaster.views.layout.stat.StatsCards
import com.example.taskmaster.views.layout.stat.TaskStatusOverview

@Composable
fun ProjectStats(
    nav: NavHostController,
    projectId: Long,
    projectsVm: ProjectsViewModel = remember { ProjectsViewModel() },
    statsVm: ProjectStatsViewModel = remember { ProjectStatsViewModel() }
) {
    val project by projectsVm.current.collectAsState()
    val isLoading by statsVm.isLoading.collectAsState()
    val error by statsVm.error.collectAsState()
    val stats by statsVm.stats.collectAsState()

    LaunchedEffect(projectId) {
        projectsVm.loadById(projectId)
        statsVm.loadProjectTasks(projectId)
    }

    Scaffold(
        topBar = {
            Column {
                ProjectTopBar(
                    title = project?.name ?: "Estadísticas",
                    onBack = { nav.popBackStack() }
                )
                ProjectMiniTabs(
                    selected = ProjectSection.STATS,
                    onSelect = {
                        when (it) {
                            ProjectSection.TASKS -> nav.navigate("projectTasks/$projectId") { launchSingleTop = true }
                            ProjectSection.STATS -> Unit
                            ProjectSection.SETTINGS -> nav.navigate("projectSettings/$projectId") { launchSingleTop = true }
                        }
                    }
                )
            }
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                !error.isNullOrBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(13.dp))

                        StatsCards(stats = stats)

                        Spacer(modifier = Modifier.height(13.dp))

                        TaskStatusOverview(stats = stats)

                        Spacer(modifier = Modifier.height(13.dp))

                        PriorityChart(stats = stats)

                    }
                }
            }
        }
    }
}
