package com.example.taskmaster.views.layout.project

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskmaster.viewmodel.data.tasks.TaskDto
import com.example.taskmaster.viewmodel.data.tasks.TaskPriority
import com.example.taskmaster.viewmodel.data.tasks.TaskStatus
import com.example.taskmaster.viewmodel.model.TasksViewModel
import com.example.taskmaster.viewmodel.sharedPreferences.Prefs
import com.example.taskmaster.viewmodel.ui.users.UsersViewModel
import com.example.taskmaster.views.layout.common.AppTopHeader
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

@Composable
fun UserStats(
    context: Context,
    nav: NavHostController,
    userId: Long,
    tasksVm: TasksViewModel = remember { TasksViewModel() },
    userVm: UsersViewModel = remember { UsersViewModel() }
) {
    val currentUser by userVm.user.collectAsState()
    val isLoading by tasksVm.isLoading.collectAsState()
    val error by tasksVm.error.collectAsState()
    val tasks by tasksVm.tasks.collectAsState()

    LaunchedEffect(userId) {
        Prefs.loadEmail(context)?.let(userVm::loadByEmail)
        tasksVm.loadByUser(userId)
    }

    val stats = remember(tasks) { buildUserTaskStats(tasks) }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                AppTopHeader(
                    user = currentUser,
                    onNotificationsClick = { nav.navigate("notification") },
                    onProfileClick = { nav.navigate("profile") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
                    ) {
                        Text(
                            text = "Member Statistics",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Overview of assigned work, progress and priorities.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
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
                        CircularProgressIndicator(color = Color(0xFFEC1926))
                    }
                }

                !error.isNullOrBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Unknown error",
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
                            Text("This member has no assigned tasks yet.")
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            UserStatsCards(stats = stats)
                            Spacer(modifier = Modifier.height(13.dp))
                            UserTaskStatusOverview(stats = stats)
                            Spacer(modifier = Modifier.height(13.dp))
                            UserPriorityChart(stats = stats)
                            Spacer(modifier = Modifier.height(24.dp))
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
