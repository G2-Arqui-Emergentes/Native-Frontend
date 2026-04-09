package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.views.layout.stat.UserPriorityChart
import com.example.taskmaster.views.layout.stat.UserStatsCards
import com.example.taskmaster.views.layout.stat.UserTaskStatusOverview

data class UserTaskStats(
    val totalTasks: Int = 0,
    val todoTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val doneTasks: Int = 0,
    val highPriorityTasks: Int = 0,
    val mediumPriorityTasks: Int = 0,
    val lowPriorityTasks: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStats(
    nav: NavHostController,
    userId: Long,
    tasksVm: TasksViewModel = remember { TasksViewModel() }
) {
    val isLoading by tasksVm.isLoading.collectAsState()
    val error by tasksVm.error.collectAsState()
    val tasks by tasksVm.tasks.collectAsState()

    LaunchedEffect(userId) {
        tasksVm.loadByUser(userId)
    }

    val stats = remember(tasks) { buildUserTaskStats(tasks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas del miembro") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    if (tasks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Este miembro aún no tiene tareas asignadas")
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(13.dp))

                            UserStatsCards(stats = stats)

                            Spacer(modifier = Modifier.height(13.dp))

                            UserTaskStatusOverview(stats = stats)

                            Spacer(modifier = Modifier.height(13.dp))

                            UserPriorityChart(stats = stats)
                        }
                    }
                }
            }
        }
    }
}

private fun buildUserTaskStats(tasks: List<TaskDto>): UserTaskStats {
    val total = tasks.size

    val todo = tasks.count { it.status == TaskStatus.TO_DO }
    val inProgress = tasks.count { it.status == TaskStatus.IN_PROGRESS }
    val done = tasks.count { it.status == TaskStatus.DONE }

    val high = tasks.count { it.priority == TaskPriority.HIGH }
    val medium = tasks.count { it.priority == TaskPriority.MEDIUM }
    val low = tasks.count { it.priority == TaskPriority.LOW }

    return UserTaskStats(
        totalTasks = total,
        todoTasks = todo,
        inProgressTasks = inProgress,
        doneTasks = done,
        highPriorityTasks = high,
        mediumPriorityTasks = medium,
        lowPriorityTasks = low
    )
}
